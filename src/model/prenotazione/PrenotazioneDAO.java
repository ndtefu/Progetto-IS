package model.prenotazione;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import model.ModelInterface;
import model.connessione.DriverManagerConnectionPool;
import model.periferica.PerifericaBean;

public class PrenotazioneDAO implements ModelInterface<PrenotazioneBean, Integer> {
	/**
	 *  ritorna, se presente nel database, una prenotazione con l'id
	 *           inserito
	 * 
	 * @param id l'id della prenotazione da ricercare
	 */
	@Override
	public PrenotazioneBean doRetrieveByKey(Integer id) {
		PrenotazioneBean bean = new PrenotazioneBean();
		String sql = "Select * FROM prenotazione WHERE id=?";
		try (Connection con = DriverManagerConnectionPool.getConnection();
				PreparedStatement statement = con.prepareStatement(sql);) {
			statement.setInt(1, id);
			System.out.println("DoRetrieveByKey" + statement);
			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				bean.setData(rs.getString("dataPrenotazione"));
				bean.setFasciaOraria(rs.getString("fasciaOraria"));
				bean.setId(rs.getInt("id"));
				bean.setQr(rs.getString("qr"));
				bean.setPostazioneId(rs.getInt("postazioneId"));
				bean.setUtenteEmail(rs.getString("utenteEmail"));
				bean.setPrezzo(rs.getFloat("prezzo"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return bean;
	}

	/**
	 *  ritorna tutte le prenotazioni presenti nel database
	 * 
	 */
	@Override
	public Collection<PrenotazioneBean> doRetrieveAll(){
		String sql = "Select * FROM prenotazione ORDER BY id desc";
		ArrayList<PrenotazioneBean> collection = new ArrayList<PrenotazioneBean>();

		try (Connection con = DriverManagerConnectionPool.getConnection();
				PreparedStatement statement = con.prepareStatement(sql);) {
			System.out.println("DoRetriveAll" + statement);
			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				PrenotazioneBean bean = new PrenotazioneBean();
				bean.setData(rs.getString("dataPrenotazione"));
				bean.setFasciaOraria(rs.getString("fasciaOraria"));
				bean.setId(rs.getInt("id"));
				bean.setQr(rs.getString("qr"));
				bean.setPostazioneId(rs.getInt("postazioneId"));
				bean.setUtenteEmail(rs.getString("utenteEmail"));
				bean.setPrezzo(rs.getFloat("prezzo"));
				collection.add(bean);
			}

			return collection;

		} catch (SQLException e) {
	
			e.printStackTrace();
			return null;
		}
	}

	/**
	 *  Termina la prenotazione disattivando il qr
	 * 
	 * @param bean Prenotazione da terminare
	 */

	public boolean terminaPrenotazione(PrenotazioneBean bean){
		String sql = "UPDATE prenotazione SET qr='' WHERE id=?";
		try (Connection con = DriverManagerConnectionPool.getConnection();
				PreparedStatement statement = con.prepareStatement(sql);) {
			System.out.println("terminaPrenotazione" + statement);
			statement.setInt(1, bean.getId());
			statement.executeUpdate();
			con.commit();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * <br>
	 * NON UTILIZZARE QUESTO METODO</br>
	 * 
	 *  Inserisce una prenotazione nel database
	 * 
	 * @param bean Prenotazione da inserire
	 * @return ritorna l'id della periferica inserita, -1 altrimenti
	 */
	@Override
	public int doSave(PrenotazioneBean bean) {
		String sql = "INSERT INTO prenotazione(dataPrenotazione,fasciaOraria,qR,utenteEmail,postazioneId,prezzo) VALUES(?,?,?,?,?,?)";
		try (Connection con = DriverManagerConnectionPool.getConnection();
				PreparedStatement statement = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);) {
			System.out.println("doSave" + statement);
			statement.setString(1, bean.getData());
			statement.setString(2, bean.getFasciaOraria());
			statement.setString(3, bean.getQr());
			statement.setString(4, bean.getUtenteEmail());
			statement.setInt(5, bean.getPostazioneId());
			statement.setFloat(6, bean.getPrezzo());
			statement.executeUpdate();
			ResultSet rs = statement.getGeneratedKeys();
			rs.next();
			con.commit();
			return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public boolean doUpdate(PrenotazioneBean bean, Integer chiave){
	
		return true;
	}

	/**
	 *  Elimina una prenotazione dal database
	 * 
	 * @param id id della prenotazione da cancellare
	 */
	@Override
	public boolean doDelete(Integer id) {
		String sql = "DELETE FROM prenotazione WHERE id=?";
		try (Connection con = DriverManagerConnectionPool.getConnection();
				PreparedStatement statement = con.prepareStatement(sql);) {
			System.out.println("doDelete " + statement);
			statement.setInt(1, id);
			statement.executeUpdate();
			con.commit();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 *  Salva una prenotazione con le annesse periferiche
	 * 
	 * @param prenotazione la prenotazione da salvare
	 * 
	 * @param periferiche  le periferiche che vengono aggiunte alla prenotazione
	 */
	public int prenotaConPeriferiche(PrenotazioneBean prenotazione, ArrayList<PerifericaBean> periferiche){
		int id = doSave(prenotazione);
		String sql = "INSERT INTO prenotazione_periferica VALUES(?,?)";
		try (Connection con = DriverManagerConnectionPool.getConnection();
				PreparedStatement statement = con.prepareStatement(sql);) {
			for (PerifericaBean perifericaBean : periferiche) {
				System.out.println("addPeriferiche " + statement);
				statement.setInt(1, id);
				statement.setString(2, perifericaBean.getNome());
				statement.executeUpdate();
				con.commit();
			}
			return id;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	public Collection<PrenotazioneBean> doRetrieveByEmail(String email){
		String sql = "SELECT * FROM prenotazione where utenteEmail=?";
		ArrayList<PrenotazioneBean> collection = new ArrayList<PrenotazioneBean>();
		try (Connection con = DriverManagerConnectionPool.getConnection();
				PreparedStatement statement = con.prepareStatement(sql);) {
			System.out.println("DoRetriveAll" + statement);
			statement.setString(1, email);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				PrenotazioneBean bean = new PrenotazioneBean();
				bean.setData(rs.getString("dataPrenotazione"));
				bean.setFasciaOraria(rs.getString("fasciaOraria"));
				bean.setId(rs.getInt("id"));
				bean.setQr(rs.getString("qr"));
				bean.setPostazioneId(rs.getInt("postazioneId"));
				bean.setUtenteEmail(rs.getString("utenteEmail"));
				bean.setPrezzo(rs.getFloat("prezzo"));
				collection.add(bean);
			}
			return collection;
		} catch (SQLException e) {
	
			e.printStackTrace();
			return null;
		}
		

	}


	
	
}
