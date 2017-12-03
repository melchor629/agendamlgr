package service;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("rest")
public class AgendaMLGApp extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> set = new HashSet<>();
        set.add(AgendamlgExceptionMapper.class);
        set.add(AgendamlgNotFoundExceptionMapper.class);
        set.add(NotAuthenticatedExceptionManager.class);
        set.add(CategoriaREST.class);
        return set;
    }
}
