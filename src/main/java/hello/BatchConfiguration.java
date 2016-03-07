package hello;

import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

  @Autowired
  public JobBuilderFactory jobBuilderFactory;

  @Autowired
  public StepBuilderFactory stepBuilderFactory;

  @Autowired
  JobExecution jobExecution;

  @Autowired
  public DataSource dataSource;

  // tag::readerwriterprocessor[]
  @Bean
  public FlatFileItemReader<Person> reader() {
    FlatFileItemReader<Person> reader = new FlatFileItemReader<Person>();
    reader.setResource(new ClassPathResource("sample-data.csv"));
    reader.setLineMapper(new DefaultLineMapper<Person>() {
      {
        setLineTokenizer(new DelimitedLineTokenizer() {
          {
            setNames(new String[] { "id", "firstName", "lastName" });
          }
        });
        setFieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {
          {
            setTargetType(Person.class);
          }
        });
      }
    });
    return reader;
  }

  @Bean
  public FlatFileItemReader<Person> reader2() {
    FlatFileItemReader<Person> reader = new FlatFileItemReader<Person>();
    reader.setResource(new ClassPathResource("sample-data2.csv"));
    reader.setLineMapper(new DefaultLineMapper<Person>() {
      {
        setLineTokenizer(new DelimitedLineTokenizer() {
          {
            setNames(new String[] { "id", "age" });
          }
        });
        setFieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {
          {
            setTargetType(Person.class);
          }
        });
      }
    });
    return reader;
  }

  @SuppressWarnings("unchecked")
  @Bean
  public ListItemReader<Person> reader3(JobExecution jobExecution) {
    final HashMap<Integer, Person> sharedData = (HashMap<Integer, Person>) jobExecution.getExecutionContext().get("data");
    final ListItemReader<Person> reader = new ListItemReader<Person>(new ArrayList<Person>(sharedData.values())) {

    };
    return reader;
  }

  @Bean
  public PersonItemProcessor processor() {
    return new PersonItemProcessor();
  }

  @Bean
  public PersonItemProcessor processorEnd() {
    return new PersonItemProcessorEnd();
  }

  @Bean
  public PersonItemProcessor processorClean() {
    return new PersonItemProcessorClean();
  }

  @Bean
  public JdbcBatchItemWriter<Person> writer() {
    JdbcBatchItemWriter<Person> writer = new JdbcBatchItemWriter<Person>();
    writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Person>());
    writer.setSql("INSERT INTO people (id, first_name, last_name, age) VALUES (:id, :firstName, :lastName, :age)"); // in memory db
    // writer.setSql("INSERT INTO people (person_id, first_name, last_name) VALUES (s_people.nextval, :firstName, :lastName)"); // oracle with s_people sequence
    writer.setDataSource(dataSource);
    return writer;
  }
  // end::readerwriterprocessor[]

  // tag::listener[]

  @Bean
  public JobExecutionListener listener() {
    return new JobCompletionNotificationListener(new JdbcTemplate(dataSource));
  }

  // end::listener[]

  // tag::jobstep[]
  @Bean
  public Job importUserJob() {
    Job job = jobBuilderFactory.get("importUserJob")
        .incrementer(new RunIdIncrementer())
        .listener(listener())
        .flow(step1())
        .next(step2())
        .next(step3())
        .end()
        .build();
    return job;
  }

  @Bean
  public Step step1() {
    return stepBuilderFactory.get("step1")
        .<Person, Person> chunk(10)
        .listener(readerListener())
        .reader(reader())
        .processor(processor())
        // .writer(writer())
        .build();
  }

  private static class PersonReaderListener implements ItemReadListener<Person> {

    @Override
    public void beforeRead() {

    }

    @Override
    public void afterRead(Person item) {
      // TODO Auto-generated method stub

    }

    @Override
    public void onReadError(Exception ex) {
      // TODO Auto-generated method stub

    }

  }

  private ItemReadListener<Person> readerListener() {
    return new PersonReaderListener();
  }

  @Bean
  public Step step2() {
    return stepBuilderFactory.get("step2")
        .<Person, Person> chunk(10)
        .reader(reader2())
        .processor(processorEnd())
        .writer(writer())
        .build();
  }

  @Bean
  public Step step3() {
    return stepBuilderFactory.get("step2")
        .<Person, Person> chunk(10)
        .reader(reader3(jobExecution))
        .writer(writer())
        .build();
  }
  // end::jobstep[]
}
