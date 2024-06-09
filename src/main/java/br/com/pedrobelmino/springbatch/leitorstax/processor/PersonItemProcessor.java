package br.com.pedrobelmino.springbatch.leitorstax.processor;

import br.com.pedrobelmino.springbatch.leitorstax.model.Person;
import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {
    @Override
    public Person process(Person person) throws Exception {
        System.out.println(person);
        return person;
    }
}
