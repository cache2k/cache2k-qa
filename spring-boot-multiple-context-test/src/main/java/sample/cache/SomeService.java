package sample.cache;

import org.springframework.stereotype.Service;

@Service
public class SomeService {

	public String getString() {
		System.out.println("Executing service method");
		return "foo";
	}

}
