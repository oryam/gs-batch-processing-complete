package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonItemProcessorClean extends PersonItemProcessor {

  private static final Logger log = LoggerFactory.getLogger(PersonItemProcessorClean.class);

  @Override
  protected void saveData(final Person transformedPerson) throws Exception {
  }

}
