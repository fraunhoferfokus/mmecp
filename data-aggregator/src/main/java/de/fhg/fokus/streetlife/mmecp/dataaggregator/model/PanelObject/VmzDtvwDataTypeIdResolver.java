package de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.jsontype.TypeIdResolver;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.map.util.ClassUtil;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by csc on 17.08.2015.
 * this class has been for testing only and is now not used anymore - can be deleted!!!
 */
public class VmzDtvwDataTypeIdResolver implements TypeIdResolver {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private static final String MY_PACKAGE =
            VmzDtvwData.class.getPackage().getName() + ".vmzdtvwdata";
    private JavaType mBaseType;

    @Override
    public void init(JavaType baseType)
    {
        mBaseType = baseType;
    }

    @Override
    public JsonTypeInfo.Id getMechanism()
    {
        return JsonTypeInfo.Id.CUSTOM;
    }

    @Override
    public String idFromValue(Object obj)
    {
        return idFromValueAndType(obj, obj.getClass());
    }

//    @Override
//    public String idFromBaseType()
//    {
//        return idFromValueAndType(null, mBaseType.getRawClass());
//    }

    @Override
    public String idFromValueAndType(Object obj, Class<?> clazz)
    {
        String name = clazz.getName();

        LOG.info("name: {}, package: {}", name, MY_PACKAGE);

        if ( name.startsWith(MY_PACKAGE) ) {
            return name.substring(MY_PACKAGE.length() + 1);
        }
        throw new IllegalStateException("class " + clazz + " is not in the package " + MY_PACKAGE);
    }

    @Override
    public JavaType typeFromId(String type)
    {
        Class<?> clazz;
        String clazzName = MY_PACKAGE + "." + type;
        try {
            clazz = ClassUtil.findClass(clazzName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("cannot find class '" + clazzName + "'");
        }
        return TypeFactory.defaultInstance().constructSpecializedType(mBaseType, clazz);
    }
}