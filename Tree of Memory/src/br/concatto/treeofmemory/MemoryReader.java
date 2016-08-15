package br.concatto.treeofmemory;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

public class MemoryReader {
	private static final long BASE_ADDRESS = 0x01489F10;
	private static final long CURRENT_OFFSET = 0x10C;
	private static final long TOTAL_OFFSET = 0x110;
	private static final int PROCESS_VM_READ = 0x0010;
	private static Kernel32 kernel = Kernel32.INSTANCE;
	private static User32 user = User32.INSTANCE;
	private IntByReference pid = new IntByReference();
	private HANDLE handle;
	private Pointer currentExperience;
	private Pointer totalExperience;
	
	public MemoryReader() {
		
	}

	public boolean initialize(String windowName) {
		HWND window = user.FindWindow(null, windowName);		
		if (user.GetWindowThreadProcessId(window, pid) == 0) {
			return false;
		}
		
		handle = kernel.OpenProcess(PROCESS_VM_READ, false, pid.getValue());
		if (handle == null) {
			return false;
		}
		
		IntByReference read = new IntByReference();
		Pointer address = new Pointer(BASE_ADDRESS);
		Memory mem = new Memory(4);
		kernel.ReadProcessMemory(handle, address, mem, (int) mem.size(), read);
		
		int value = mem.getInt(0);
		currentExperience = new Pointer(value + CURRENT_OFFSET);
		totalExperience = new Pointer(value + TOTAL_OFFSET);
		return read.getValue() == 4;
	}
	
	public int readCurrentExperience() {
		return readInt(handle, currentExperience);
	}
	
	public int readTotalExperience() {
		return readInt(handle, totalExperience);
	}
	
	private static int readInt(HANDLE handle, Pointer address) {
		Memory mem = new Memory(4);
		IntByReference read = new IntByReference();
		kernel.ReadProcessMemory(handle, address, mem, 4, read);
		
		return mem.getInt(0);
	}
}
