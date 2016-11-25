package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;

import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.EngineType;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import static java.lang.annotation.ElementType.*;

/**
 * Created by bdi on 21/12/14.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, METHOD, FIELD})
public @interface ResponseParseEngineMethod {

    EngineType value();
}
