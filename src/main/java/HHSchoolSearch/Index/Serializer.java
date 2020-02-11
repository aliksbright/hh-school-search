package HHSchoolSearch.Index;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Serializer
{
    private static Gson initGson()
    {
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }

    // Сначала задумывалось, что эти методы будут сильно различаться. Пока оставил.

    public static String jsonAllDocs()
    {
        var gson = initGson();
        var allDocs = AllDocs.getSingleton();

        return gson.toJson(allDocs);
    }

    public static String jsonAllTerms()
    {
        var gson = initGson();
        var allTerms = AllTerms.getSingleton();

        return gson.toJson(allTerms);
    }

    public static String jsonInvertedDocs()
    {
        var gson = initGson();
        var invertedDocs = InvertedDocs.getSingleton();

        return gson.toJson(invertedDocs);
    }

    public static String jsonInvertedTerms()
    {
        var gson = initGson();
        var invertedTerms = InvertedTerms.getSingleton();

        return gson.toJson(invertedTerms);
    }

    public static AllDocs loadAllDocs(String json)
    {
        var gson = initGson();
        return gson.fromJson(json, AllDocs.class);
    }

    public static AllTerms loadAllTerms(String json)
    {
        var gson = initGson();
        return gson.fromJson(json, AllTerms.class);
    }

    public static InvertedDocs loadInvertedDocs(String json)
    {
        var gson = initGson();
        return gson.fromJson(json, InvertedDocs.class);
    }

    public static InvertedTerms loadInvertedTerms(String json)
    {
        var gson = initGson();
        return gson.fromJson(json, InvertedTerms.class);
    }
}
