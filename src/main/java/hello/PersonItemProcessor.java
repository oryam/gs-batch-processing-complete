package hello;

import static org.apache.commons.lang3.StringUtils.upperCase;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

  private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

  protected HashMap<Integer, Person> sharedData;

  protected NullAwareBeanUtilsBean nullAwareBeanUtilsBean = new NullAwareBeanUtilsBean();

  @Override
  public Person process(final Person person) throws Exception {
    Person transformedPerson = new Person();
    nullAwareBeanUtilsBean.copyProperties(transformedPerson, person);
    transformedPerson.setFirstName(upperCase(person.getFirstName()));
    transformedPerson.setLastName(upperCase(person.getLastName()));

    log.info("Converting (" + person + ") into (" + transformedPerson + ")");

    saveData(transformedPerson);

    return transformedPerson;
  }

  @BeforeStep
  @SuppressWarnings("unchecked")
  public void loadData(StepExecution stepExecution) {
    sharedData = (HashMap<Integer, Person>) stepExecution.getJobExecution().getExecutionContext().get("data");
    log.info(Arrays.toString(sharedData.values().toArray()));
  }

  public static class NullAwareBeanUtilsBean extends BeanUtilsBean {
    @Override
    public void copyProperty(Object dest, String name, Object value) throws IllegalAccessException, InvocationTargetException {
      if (value == null)
        return;
      super.copyProperty(dest, name, value);
    }
  }

  protected void saveData(final Person transformedPerson) throws Exception {
    log.info(transformedPerson.toString());
    int personId = transformedPerson.getId();
    if (sharedData.containsKey(personId)) {
      log.info("found in data : " + personId);
      nullAwareBeanUtilsBean.copyProperties(sharedData.get(personId), transformedPerson);
    } else {
      sharedData.put(personId, transformedPerson);
    }
    log.info(sharedData.get(personId).toString());
  }

}
