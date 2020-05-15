package it.polito.tdp.crimes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import it.polito.tdp.crimes.model.Arco;
import it.polito.tdp.crimes.model.Event;
import it.polito.tdp.crimes.model.Reato;


public class EventsDao {
	
	public List<Event> listAllEvents(){
		String sql = "SELECT * FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<String> categorieReato(){
		
		
		String sql = "SELECT distinct `offense_category_id` " + 
						"FROM events ORDER BY offense_category_id" ;
		
		List<String> categorie = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				
				categorie.add(res.getString("offense_category_id"));
			}
			
			conn.close();
			return categorie ;

		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}

		
	}
	
	public List<String> mesi(){
		
		
		String sql = "SELECT distinct Month(`reported_date`) as mese " + 
				"FROM events " + 
				"ORDER BY mese ASC" ;
		
		List<String> mesi = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				
				mesi.add( Month.of(res.getInt("mese")).toString());
				//getDisplayName(TextStyle.FULL, Locale.ITALY)); 
				//=> se trasformo int in stringhe in italiano, poi nel fare da stringa a int, non riesce. Devono essere in inglese
				
				
			}
			
			conn.close();
			return mesi ;

		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}

		
	}
	
	public List<Reato> vertici(int mese, String categoria){
		
		String sql = "SELECT  distinct events.`offense_type_id` as tipo " + 
				"FROM events " + 
				"WHERE offense_category_id =? and month(reported_date) =? ";
		
		List<Reato> vertici = new LinkedList<>() ;
		
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			st.setString(1, categoria);
			st.setInt(2, mese);
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				
					vertici.add(new Reato(res.getString("tipo")));
			}
			
			conn.close();
			return vertici ;

		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}
	
	}
	
	public Integer quartieriDiversi(Reato r1, Reato r2, int mese) {
		
		String sql ="SELECT  count(distinct e1.`neighborhood_id`) as quartieri " + 
				"FROM events e1, events e2 " + 
				"WHERE e1.`offense_type_id` =? and e2.`offense_type_id` =? and " + 
				"month(e1.reported_date) =?  and month(e2.reported_date) =? and " + 
				"e1.`neighborhood_id` = e2.`neighborhood_id` " ; 
		
		//vincolo del mese rimane, GIUSTO?????
		
		Integer quartieri = null;
		
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			st.setString(1, r1.getTipoReato());
			st.setString(2, r2.getTipoReato());
			st.setInt(3, mese);
			st.setInt(4, mese);
			
			ResultSet res = st.executeQuery() ;
			
			if(res.next()) {
				quartieri = res.getInt("quartieri");
				
			}
			
			conn.close();
			return quartieri;

		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}
		
	}
	
	/**Query :
	 * 
	 * seleziono tipo1 tipo2, quartieriDiversi
	 * 
	 * duplico la tabella: selezionando solo i vertici (condizioni di mese + categoria) in entrambe le tabelle
	 * tipo 1 < tipo 2 ==> evitare A-B, B-A
	 * stesso queartiere
	 * 
	 * @param mese
	 * @param categoria
	 * @return
	 */
	public List<Arco> coppiaReati(int mese, String categoria, Map<String, Reato> idMap){
		
		String sql = "SELECT  e1.`offense_type_id`, e2.`offense_type_id`, count(distinct e1.neighborhood_id) as quartieri " + 
				"FROM events e1, events e2 " + 
				"WHERE (e1.offense_category_id =? and month(e1.reported_date) =?) and " + 
				"(e2.offense_category_id =? and month(e2.reported_date) =?) and " + 
				"e1.`offense_type_id` < e2.`offense_type_id` and " + 
				"e1.`neighborhood_id` = e2.`neighborhood_id`  " + 
				"GROUP BY e1.`offense_type_id`, e2.`offense_type_id` ";
		
		List<Arco> archi = new LinkedList<>() ;
		
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			st.setString(1, categoria);
			st.setInt(2, mese);
			st.setString(3, categoria);
			st.setInt(4, mese);
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
			/*	--------   IDMAP!!!!
					Reato r1 = new Reato( res.getString("e1.offense_type_id"));
					Reato r2 = new Reato( res.getString("e2.offense_type_id"));
			*/	
					Reato r1 = idMap.get(res.getString("e1.offense_type_id"));
					Reato r2 = idMap.get(res.getString("e2.offense_type_id"));
					
					archi.add(new Arco(r1, r2, res.getInt("quartieri")));
			}
			
			conn.close();
			return archi ;

		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}
	
		
		
		
		
		
	}

}
