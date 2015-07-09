package addLines;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.StringTokenizer;

public class AddLines
{

	// Read a list of numbers from stdin and write the sum to stdout
	public static void main(String[] args)
	{
		try {
			Reader reader = new InputStreamReader(System.in);
			BufferedReader breader = new BufferedReader(reader);
			String lineRead = null;
			while ((lineRead = breader.readLine()) != null) {
				int sum = 0;
				StringTokenizer st = new StringTokenizer(lineRead);
				while (st.hasMoreTokens()) {
					String nums = st.nextToken();
					try {
						int temp = Integer.parseInt(nums);
						sum += temp;
					}
					catch (NumberFormatException ex) {
						System.err.println("Ignoring " + nums);
						continue;
					}
				}
				System.out.println(sum);
				System.out.flush();
			}

		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
