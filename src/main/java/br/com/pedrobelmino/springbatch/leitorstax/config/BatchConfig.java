package br.com.pedrobelmino.springbatch.leitorstax.config;

import br.com.pedrobelmino.springbatch.leitorstax.model.Person;
import br.com.pedrobelmino.springbatch.leitorstax.processor.PersonItemProcessor;
import br.com.pedrobelmino.springbatch.leitorstax.tasklet.RecuperarArquivoTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public StaxEventItemReader<Person> reader() {
        Jaxb2Marshaller personMarshaller = new Jaxb2Marshaller();
        personMarshaller.setClassesToBeBound(Person.class);

        return new StaxEventItemReaderBuilder<Person>()
                .name("personItemReader")
                .resource(new FileSystemResource("/tmp/persons.xml"))
                .addFragmentRootElements("person")
                .unmarshaller(personMarshaller)
                .build();
    }

    @Bean
    public ItemProcessor<Person, Person> processor() {
        return new PersonItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO person (first_name, last_name) VALUES (:firstName, :lastName)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job importUserJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                             StaxEventItemReader<Person> reader, ItemProcessor<Person, Person> processor,
                             JdbcBatchItemWriter<Person> writer,  RecuperarArquivoTasklet tasklet) {

        Step step1 = stepBuilderFactory.get("step1").tasklet(tasklet).build();

        Step step2 = stepBuilderFactory.get("step2")
                .<Person, Person>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();



        return jobBuilderFactory.get("importJob")
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .next(step2)
                .build();
    }

}
