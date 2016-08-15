package br.concatto.socket;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.function.BiConsumer;

public class Server implements Closeable {	
	private int port;
	
	private BiConsumer<Byte[], Object> dataReceivedConsumer;
	private ServerSocketChannel server;
	private Selector selector;
	
	public Server(int port) throws IOException {
		this.port = port;
		
		server = ServerSocketChannel.open();
		server.bind(new InetSocketAddress("0.0.0.0", port));
		server.configureBlocking(false);
		
		selector = Selector.open();
		server.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	public void begin() throws IOException {
		ByteBuffer buffer = ByteBuffer.allocateDirect(128);
		
		while (server.isOpen()) {
			int keys = selector.select();
			if (keys == 0) continue;
			
			for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext(); /* ... */) {
				SelectionKey key = it.next();
				it.remove();
				
				if (key.isAcceptable()) {
					/* If there are clients waiting to be accepted, accept */
					SocketChannel channel = server.accept();
					channel.configureBlocking(false).register(selector, SelectionKey.OP_READ).attach(channel.getRemoteAddress().toString());
				} else if (key.isReadable()) {
					/* If there is data available, read */
					SocketChannel s = (SocketChannel) key.channel();
					int r = -1;
					try {
						r = s.read(buffer);
					} catch (IOException e) {
						System.out.println("Client gone without even saying goodbye.");
					}
					
					/* If EOF was reached, close the channel */
					if (r == -1) {
						System.out.println("Socket closed.");
						s.close();
					} else if (r != 0 && dataReceivedConsumer != null) {
						/* If something was read and someone wants to be notified, notify */
						dataReceivedConsumer.accept(absorbBuffer(buffer), key.attachment());
					}
				}
			}
		}
	}
	
	private static Byte[] absorbBuffer(ByteBuffer buffer) {
		buffer.flip();
		Byte[] bytes = new Byte[buffer.limit()];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = buffer.get();
		}
		buffer.compact();
		return bytes;
	}
	
	public int getPort() {
		return port;
	}
	
	public void onDataReceived(BiConsumer<Byte[], Object> consumer) {
		dataReceivedConsumer = consumer;
	}
	
	public boolean isOpen() {
		return server.isOpen();
	}

	@Override
	public void close() throws IOException {
		selector.close();
		server.close();
	}

	public void writeBack(Object identifier, byte[] data) {
		selector.keys().stream()
			.filter(key -> key.attachment() != null)
			.filter(key -> key.attachment().equals(identifier))
			.forEach(key -> writeToChannelRethrowing(key, data));
	}
	
	private void writeToChannelRethrowing(SelectionKey key, byte[] data) {
		SocketChannel channel = (SocketChannel) key.channel();
		try {
			channel.write(ByteBuffer.wrap(data));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
