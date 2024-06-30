package junit.didan.calculate;

public class Calculate {
	public int add(Integer... numbers) {
		int sum = 0;
		for (int i : numbers) {
			sum += i;
		}
		return sum;
	}
}
