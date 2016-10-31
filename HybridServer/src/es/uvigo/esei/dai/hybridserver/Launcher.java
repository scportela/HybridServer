package es.uvigo.esei.dai.hybridserver;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class Launcher {
	public static void main(String[] args) {
		
		boolean map=false;
		if(args.length==1){
			try {
				FileReader reader=new FileReader(args[0]);
				Properties prop=new Properties();
				prop.load(reader);
				reader.close();
				HybridServer hybrid=new HybridServer(prop);
				hybrid.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("Problemas con el fichero de entrada"+e.getMessage());
			}
		}else{
			if(map){
				ServerMap pages=new ServerMap(new HashMap<>());
				HybridServer hybrid=new HybridServer(pages.getDB());
				hybrid.start();
			}else{
				HybridServer hybrid=new HybridServer();
				hybrid.start();
			}
		}
	}
}
