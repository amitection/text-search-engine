package processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.ir.searchengine.Constants;

/**
 * Generate result file for Cran
 * 
 * @author amit
 */
public class GenerateResultsFile {

	public static void main(String[] args) {

		Path currentRelativePath = Paths.get("");
		System.out.println(currentRelativePath.toAbsolutePath().toString());
		
		String filename = Constants.PATH_TO_RESULTS;

		File file = new File(filename);
		try (BufferedReader br = new BufferedReader(new FileReader(file));
				FileWriter fileWriter = new FileWriter(filename+"new");
				PrintWriter printWriter = new PrintWriter(fileWriter);) {

			String line;
			while ((line = br.readLine()) != null) {
				String[] lineArray = line.split(" ");
				
				int relevance = 0;
				if( Integer.parseInt(lineArray[2]) > 3)
					relevance = 0;
				else
					relevance = 1;
				printWriter.print(lineArray[0]+" 0 "+lineArray[1]+" "+relevance);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
