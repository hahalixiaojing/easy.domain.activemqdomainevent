package easy.domain.activemqdomainevent;

abstract class ClassUtils {

    public static String getShortName(Class<?> cls) {

        String[] names = cls.getName().split("\\.");

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < names.length; i++) {

            if (i == names.length - 1) {
                stringBuilder.append(names[i]);
            }
            else {
                stringBuilder.append(names[i].substring(0, 1));
                stringBuilder.append(".");
            }


        }
        return stringBuilder.toString();
    }
}
