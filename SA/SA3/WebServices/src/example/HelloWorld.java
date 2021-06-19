package example;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

@WebService
public class HelloWorld {
  public String sayHelloWorldFrom(String from) {
    String result = "Hello, world, from " + from;
    System.out.println(result);
    return result;
  }
  public double add(double a, double b) {
    return a + b;
  }
  public double minus(double a, double b) {
    return a - b;
  }

  public double multiply(double a, double b) {
    return a * b;
  }

  public double divide(double a, double b) {
    return a / b;
  }

  public double power(double a) {
    return a*a;
  }

  public double sqrt(double a) {
    if (a < 0) {
      System.out.println("开方数要大于0");
      return 0;
    } else {
      return Math.sqrt(a);
    }
  }

  public static void main(String[] args) {
    Endpoint.publish("http://localhost:8083/test",new HelloWorld());
  }
}
