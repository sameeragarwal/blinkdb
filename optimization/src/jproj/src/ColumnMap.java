import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ColumnMap {
	private Map<String, Integer> name_to_number = null;
	private Map<Integer, String> number_to_name = null;
	
	
	public ColumnMap(String columnMapFilename) throws IOException {
		name_to_number = new HashMap<String, Integer>();
		number_to_name = new HashMap<Integer, String>();
		BufferedReader bis;
		bis = new BufferedReader(new FileReader(columnMapFilename));
		String input;
		int lineNo = 0;
		while ((input=bis.readLine())!=null) {
			++lineNo;
			String[] s=input.split("\\s");
			name_to_number.put(s[0], lineNo);
			number_to_name.put(lineNo, s[0]);
		}
	}


	public Map<String, Integer> getName2NumberMapping() {
		return name_to_number;
	}


	public Map<Integer, String> getNumber2NameMapping() {
		return number_to_name;
	}
	
	
}
