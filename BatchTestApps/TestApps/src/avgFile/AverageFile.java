package avgFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.StringTokenizer;

public class AverageFile
{

	public static void main(String[] args)
	{
		try {
			int sum = 0;
			int total = 0;

			Reader reader = new InputStreamReader(System.in);
			BufferedReader breader = new BufferedReader(reader);
			String lineRead = null;
			while ((lineRead = breader.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(lineRead);
				while (st.hasMoreTokens()) {
					String nums = st.nextToken();
					try {
						int temp = Integer.parseInt(nums);
						sum += temp;
						total++;
					}
					catch (NumberFormatException ex) {
						System.err.println("Ignoring " + nums);
						continue;
					}
				}
			}
			double avg = (total > 0) ? sum/total : 0;
			System.out.println(avg);
			System.out.flush();
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
