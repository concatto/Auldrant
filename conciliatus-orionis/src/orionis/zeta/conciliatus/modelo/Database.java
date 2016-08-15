package orionis.zeta.conciliatus.modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Database {
	private Connection conexao;
	private PreparedStatement obterInscritos;
	
	public Database() {
		
	}
	
	public void conectar(String host, String usuario, String senha) throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conexao = DriverManager.getConnection("jdbc:mysql://" + host, usuario, senha);
			obterInscritos = conexao.prepareStatement("select * from Inscritos");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public List<String[]> getInscritos() {
		try {
			List<String[]> lista = new ArrayList<String[]>();
			ResultSet resultado = obterInscritos.executeQuery();
			
			while (resultado.next()) {
				String[] array = {resultado.getString("Nome"), resultado.getString("Email")};
				lista.add(array);
			}
			
			return lista;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
