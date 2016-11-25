package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;

import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.EngineType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bdi on 21/12/14.
 */
@ApplicationScoped
public class ResponseEngineFactory implements Serializable {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	private class EngineMap {
		private Map<String, Class> map;

		EngineMap() {
			map = new HashMap<String, Class>();
		}

		void addEngine(ResponseParseEngine engine) {
			if (engine == null) {return;}

			String useCaseName = engine.getUseCaseID();
			if (useCaseName == null || useCaseName.isEmpty()) {return;}

			if (!(ResponseParseEngine.class.isAssignableFrom(engine.getClass())))
				throw new RuntimeException("ResponseParseEngineFactory: tried to register a class that is not a ResponseparseEngine for useCase: " + useCaseName);

			if( map.get(useCaseName) != null )
				throw new RuntimeException("ResponseParseEngineFactory: Tried to register a second ResponseparseEngine class for useCase: " + useCaseName);

			// else
			map.put(useCaseName, engine.getClass());
		}

		Class lookupEngine( String useCaseName ) {
			LOG.info("FACTORY lookupEngine(\"" + useCaseName + "\")");
			return map.get( useCaseName );
		}
	}

	// maps useCaseNames to class of responsible ResponseParseEngine
	private EngineMap engineMap;

	// the one and only ResponseEngineFactory object
	private static ResponseEngineFactory ref = new ResponseEngineFactory();

	// call ResponseEngineFactory.getTheFactory() to access the one and only factory!
	public static ResponseEngineFactory getTheFactory() {
		return ref;
	}

	// hide the ResponseEngineFactory default constructor because this class shall be a Singleton
	private ResponseEngineFactory() {
		this.engineMap = new EngineMap() {
			{
				addEngine(new FiWareEngine());
				addEngine(new RovDemoEngine());
				addEngine(new RovParkingStationEngine());
				addEngine(new TreLiveParkingStationEngine());
				addEngine(new BerCO2Engine());
				addEngine(new BerBikeAccidentsEngine());
				addEngine(new TamParkingStationEngine());
				addEngine(new BerBikeSharingEngine());
				addEngine(new BerStaticBikeSharingDataEngine());
				addEngine(new BerModalSplitEngine());
			}};
	}

	public ResponseParseEngine makeResponseParseEngine( String useCaseName ) throws InstantiationException, IllegalAccessException {
		LOG.info("FACTORY wants to make new instance for use case: " + useCaseName);
		Class cla = this.engineMap.lookupEngine(useCaseName);
		if (cla == null) {
			throw new java.lang.InstantiationException();
		}
		//else
		return this.makeEngine(cla);
	}


	public ResponseParseEngine makeResponseParseEngine( Class cla ) throws InstantiationException, IllegalAccessException {
		if (cla == null || !(ResponseParseEngine.class.isAssignableFrom(cla))) {
			throw new java.lang.InstantiationException();
		}
		//else
		return this.makeEngine(cla);
	}


	private ResponseParseEngine makeEngine( Class cla ) throws IllegalAccessException, InstantiationException {
		LOG.info("FACTORY making new instance of class: " + cla.toString());
		return (ResponseParseEngine) cla.newInstance();
	}


    @Produces
    @ResponseParseEngineMethod(EngineType.FIWARE)
    public ResponseParseEngine getFiWareEngine() {
        return new FiWareEngine();
    }

	@Produces
	@ResponseParseEngineMethod(EngineType.ROVDEMO)
	public ResponseParseEngine getRovDemoEngine() {
		RovDemoEngine engine = new RovDemoEngine();
		engine.readExampleData();
		return engine;
	}

	@Produces
	@ResponseParseEngineMethod(EngineType.ROVPARKINGSTATIONS)
	public ResponseParseEngine getRovParkingStationEngine() {
		return new RovParkingStationEngine();
	}

	@Produces
	@ResponseParseEngineMethod(EngineType.TRELIVEPARKINGSTATIONS)
	public ResponseParseEngine getTreLiveParkingStationEngine() {
		return new TreLiveParkingStationEngine();
	}

	@Produces
	@ResponseParseEngineMethod(EngineType.BERCO2)
	public ResponseParseEngine getBerCO2Engine() {
		return new BerCO2Engine();
	}


}
