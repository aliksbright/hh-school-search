import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Test1 {

    @Test
    public void whenSplit_thenCorrect() {
        String s = "112; to Baeldu; ng dsasdasd eadfasfd";
        var array = s.split(";", 2);
        var words = array[1].split(" ");
        assertEquals(2,  array.length);
    }

    @Test
    public void parseLong() {
        var str = "1478";
        long num = Long.parseLong(str);
        assertEquals(1478, num);

    }


}
