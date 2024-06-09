package br.com.pedrobelmino.springbatch.leitorstax.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class RecuperarArquivoTasklet implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        var personsResource = RecuperarArquivoTasklet.class.getResource("/persons.xml");
        Files.copy(Path.of(personsResource.getPath()), new FileOutputStream("/tmp/persons.xml"));
        return RepeatStatus.FINISHED;
    }
}
