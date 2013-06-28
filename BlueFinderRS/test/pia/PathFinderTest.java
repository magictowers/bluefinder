package pia;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import org.junit.Test;

public class PathFinderTest {

	@Test
	/*public void test() throws UnsupportedEncodingException, ClassNotFoundException, SQLException {
		BipartiteGraphGenerator bgg = PIAConfigurationBuilder.interlanguageWikipedia(5);
		BipartiteGraphPathGenerator.resetTables();
		bgg.generateBiGraph("Abeja", "Queen");
		bgg.generateBiGraph("Abeja", "Charles_Darwin");
		bgg.generateBiGraph("Abeja", "Atanasio");
	}*/
	
	public static void main(String[] args) throws UnsupportedEncodingException, ClassNotFoundException, SQLException {
		System.out.println("Running...");
		BipartiteGraphGenerator bgg = PIAConfigurationBuilder.interlanguageWikipedia(5);
		BipartiteGraphPathGenerator.resetTables();
		//bgg.generateBiGraph("Abeja", "Queen");
		//bgg.generateBiGraph("Abeja", "Charles_Darwin");
		//bgg.generateBiGraph("Abeja", "Atanasio");
		//bgg.generateBiGraph("Abeja", "Mayo_francés");
		bgg.generateBiGraph("Mayo_francés", "François_Mitterrand");
		}

}
