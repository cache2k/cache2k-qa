package sample.cache;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("serial")
public class Country implements Serializable {

  private static AtomicLong COUNT = new AtomicLong(0);

  private final long serialNumber = COUNT.incrementAndGet();
  private final String code;

  public Country(String code) {
    this.code = code;
  }

  public String getCode() {
    return this.code;
  }

  public long getSerialNumber() {
    return serialNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Country country = (Country) o;

    return this.code.equals(country.code);
  }

  @Override
  public int hashCode() {
    return this.code.hashCode();
  }

}
