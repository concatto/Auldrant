package epsilon.orionis.aether.controle;

import java.util.List;

import epsilon.orionis.aether.modelo.EnumProcessos;
import epsilon.orionis.aether.modelo.Filtro;

public class DirecionadorFiltro {
	private DirecionadorMensagens principal;
	private DirecionadorMensagens assist;
	private Filtro filtro;
	
	public DirecionadorFiltro(DirecionadorMensagens principal, DirecionadorMensagens assist) {
		this.principal = principal;
		this.assist = assist;
		filtro = new Filtro();
	}
	
	public void encaminharMensagem(List<String> recebido, EnumProcessos processo) {
		int indice = 1;
		for (String string : recebido) {
			encaminharMensagem(string, processo, recebido.size(), indice);
			indice++;
		}
	}
	
	public void encaminharMensagem(String recebido, EnumProcessos processo, int tamanho, int indice) {
		DirecionadorMensagens destino;
		recebido = filtro.consertarCaracteres(recebido);
		recebido = recebido.trim();
		Object mensagem = null;
		switch (processo) {
		case FLUSHDNS:
			mensagem = filtro.filtrarFlush(recebido);
			destino = principal;
			break;
		case GATEWAY:
			mensagem = filtro.filtrarGateway(recebido);
			destino = principal;
			break;
		case PING:
			mensagem = filtro.filtrarPing(recebido);
			destino = principal;
			break;
		case TRACERT:
			mensagem = filtro.filtrarTracert(recebido);
			destino = principal;
			break;
		case RELEASE:
			mensagem = filtro.filtrarRelease(recebido);
			destino = principal;
			break;
		case RENEW:
			mensagem = filtro.filtrarRenew(recebido, tamanho, indice);
			destino = principal;
			break;
		case NETINFO:
			mensagem = filtro.filtrarInfo(recebido);
			destino = assist;
			break;
		case NETSET:
			mensagem = filtro.filtrarSet(recebido, tamanho);
			destino = assist;
			break;
		case NETSHOW:
			mensagem = filtro.filtrarShow(recebido);
			destino = assist;
			break;
		default:
			return;
		}
		
		filtro.limparSaida();
		destino.enviarMensagem(mensagem);
		return;
	}

	public String getGateway() {
		return filtro.buscarGateway();
	}
}