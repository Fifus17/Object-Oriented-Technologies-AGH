package pl.edu.agh.school.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import pl.edu.agh.school.persistence.IPersistanceManager;
import pl.edu.agh.school.persistence.SerializablePersistenceManager;

public class SchoolModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IPersistanceManager.class).to(SerializablePersistenceManager.class);
        bind(String.class).annotatedWith(Names.named("teachers")).toInstance("guice-teachers.dat");
        bind(String.class).annotatedWith(Names.named("students")).toInstance("guice-classes.dat");
    }
}
