import java.util.Arrays;
import java.util.Optional;

/**
 * @author BO
 * @date 2020-12-31 10:07
 * @since 2020/12/31
 **/
public class TestCode {
	public static void main(String[] args) {
		String[] s = {"a", "b", "c"};
		Optional<String> d = Arrays.stream(s).filter(f -> f.equals("c")).findFirst();

		System.out.println(d.isPresent());
	}
}
