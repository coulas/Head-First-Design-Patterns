package headfirst.designpatterns.adapter.ducks;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.assertj.core.api.SoftAssertions;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(JUnitParamsRunner.class)
public class DucksShould {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private PrintStream mockedOut;
    @Mock
    private Random random;

    @Captor
    private ArgumentCaptor<String> messages;
    private PrintStream originalOut;
    private SoftAssertions should;

    @Before
    public void setup() {
        should = new SoftAssertions();
        originalOut = System.out;
        System.setOut(mockedOut);
    }

    @After
    public void cleanup() {
        System.setOut(originalOut);
        should.assertAll();
    }

    @Test
    @Parameters(method = "ducksProvider")
    public void ducks_are_doing_ducky_stuff(Duck duck) {
        should.assertThat(duck).isInstanceOf(Duck.class);

        doNothing().when(mockedOut).println(messages.capture());

        duck.fly();
        should.assertThat(messages.getAllValues()).allMatch(s -> s.contains("flying"));

        duck.quack();
        should.assertThat(messages.getValue()).matches(".*(Quack|Gobble).*");
    }

    public Duck[] ducksProvider() {
        return new Duck[]{
                new MallardDuck(),
                new TurkeyAdapter(new WildTurkey()),
        };
    }

    @Test
    public void turkeys_are_doing_turky_stuff() {
        // JunitParams calls provider methods before mockito inits its mock, so using parameters here implies a rand set at null instead of mocked random...
        for (Turkey turkey : turkeysProvider()) {
            messages = ArgumentCaptor.forClass(String.class);
            should.assertThat(turkey)
                    .describedAs(turkey.getClass().getSimpleName())
                    .isInstanceOf(Turkey.class);

            doNothing().when(mockedOut).println(messages.capture());
            doReturn(0).when(random).nextInt(anyInt()); // is equal to return provided by default mock configuration : RETURNS_SMART_NULLS
            turkey.fly();
            should.assertThat(messages.getValue())
                    .describedAs(turkey.getClass().getSimpleName())
                    .contains("flying");

            turkey.gobble();
            should.assertThat(messages.getValue())
                    .describedAs(turkey.getClass().getSimpleName())
                    .matches(".*(Quack|Gobble).*");
        }
    }

    public Turkey[] turkeysProvider() {
        DuckAdapter adaptedTurkey = new DuckAdapter(new MallardDuck());
        adaptedTurkey.rand = this.random;
        return new Turkey[]{
                new WildTurkey(),
                adaptedTurkey,
        };
    }
}
