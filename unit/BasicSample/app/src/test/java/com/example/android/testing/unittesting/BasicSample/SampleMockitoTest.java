package com.example.android.testing.unittesting.BasicSample;


import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class SampleMockitoTest {

    @Mock
    Clazz mock;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void TestMockedList() {
        //mock creation
        List mockedList = mock(List.class);

        //using mock object
        mockedList.add("one");
        mockedList.clear();

        //verification:
        // check that method add() was called with parameter "one"
        verify(mockedList).add("one");
        // check that method clear() was called on the mockedLists
        verify(mockedList).clear();
    }

    @Test
    public void testMockedListStubbing() {
        /* By default,for all methods that return a value, a mock will return either null, primitive/wrapper
        or an empty collecion, as appropriate (Stubbing). Stubbing can be overridden.
        Once stubbed, the method will always return a stubbed value, regardless of how many times it was called.
        Last stubbing is more important (order of stubbing matters) */

        //You can mock concrete classes, not just interfaces
        LinkedList mockedList = mock(LinkedList.class);

        //stubbing
        // when method get() is called with parameter '0' then return string "first"
        when(mockedList.get(0)).thenReturn("first");
        // when method get(1) is called on the mock, then throw a runtime exception
        when(mockedList.get(1)).thenThrow(new RuntimeException());

        //following prints "first"
        System.out.println(mockedList.get(0));

        //following throws runtime exception
        System.out.println(mockedList.get(1));

        //following prints "null" because get(999) was not stubbed (default value)
        System.out.println(mockedList.get(999));

        //Although it is possible to verify a stubbed invocation, usually it's just redundant
        //If your code cares what get(0) returns, then something else breaks (often even before verify() gets executed).
        //If your code doesn't care what get(0) returns, then it should not be stubbed. Not convinced? See here.
        verify(mockedList).get(0);
    }

    @Test
    public void testArgumentMatchersDemo() {
        LinkedList mockedList = mock(LinkedList.class);

        //stubbing using built-in anyInt() argument matcher
        // if int type (value doesn't matter) is passed, then return string "element"
        // ACHTUNG! If you are using argument matchers, all arguments have to be provided by matchers.
        when(mockedList.get(anyInt())).thenReturn("element");
        when(mockedList.get(3)).thenReturn("element #3");
        System.out.println(mockedList.get(4214));
        System.out.println(mockedList.get(3));

        //stubbing using custom matcher (let's say isValid() returns your own matcher implementation):
        // MyMatcher: will return true if value is "Vasja", false for everyone else
        when(mockedList.contains(argThat(new MyMatcher()))).thenReturn(true);
        boolean listContaintsMyName = mockedList.contains("Vasja");
        boolean listContainsAlexey = mockedList.contains("Alexey");
        //following prints "element"
        System.out.println(mockedList.get(999));

        //you can also verify using an argument matcher
        // verify that mockedList's get() method was called with some int type 3 times
        verify(mockedList, times(3)).get(anyInt());
        // did Vasja search for his name in the list?
        verify(mockedList).contains("Vasja");

        //argument matchers can also be written as Java 8 Lambdas
        // check if a string with length bigger than 5 was called
//        verify(mockedList).add(argThat(someString -> someString.length() > 5));

    }

    @Test
    public void testExactNumberOfInvocations() {
        LinkedList mockedList = mock(LinkedList.class);

        //using mock
        mockedList.add("once");

        mockedList.add("twice");
        mockedList.add("twice");

        mockedList.add("three times");
        mockedList.add("three times");
        mockedList.add("three times");

        //following two verifications work exactly the same - times(1) is used by default
        verify(mockedList).add("once");
        verify(mockedList, times(1)).add("once");

        //exact number of invocations verification
        verify(mockedList, times(2)).add("twice");
        verify(mockedList, times(3)).add("three times");

        //verification using never(). never() is an alias to times(0)
        verify(mockedList, never()).add("never happened");

        //verification using atLeast()/atMost()
        verify(mockedList, atLeastOnce()).add("three times");
        verify(mockedList, atLeast(2)).add("three times");
        verify(mockedList, atMost(5)).add("three times");


        // Stubbing void methods with exceptions
        doThrow(new RuntimeException()).when(mockedList).clear();
        //following throws RuntimeException:
//        mockedList.clear();
    }

    @Test
    public void testVerificationInOrder() {
        // A. Single mock whose methods must be invoked in a particular order
        List singleMock = mock(List.class);

        //using a single mock
        singleMock.add("was added first");
        singleMock.add("was added second");

        //create an inOrder verifier for a single mock
        InOrder inOrder = inOrder(singleMock);

        //following will make sure that add is first called with "was added first, then with "was added second"
        inOrder.verify(singleMock).add("was added first");
        inOrder.verify(singleMock).add("was added second");

        // B. Multiple mocks that must be used in a particular order
        List firstMock = mock(List.class);
        List secondMock = mock(List.class);

        //using mocks
        firstMock.add("was called first");
        secondMock.add("was called second");

        //create inOrder object passing any mocks that need to be verified in order
        inOrder = inOrder(firstMock, secondMock);

        //following will make sure that firstMock was called before secondMock
        inOrder.verify(firstMock).add("was called first");
        inOrder.verify(secondMock).add("was called second");

        // Oh, and A + B can be mixed together at will
    }

    @Test
    public void testInterractionNeverHappened() {
        List mockOne = mock(List.class);
        List mockTwo = mock(List.class);
        List mockThree = mock(List.class);

        //using mocks - only mockOne is interacted
        mockOne.add("one");

        //ordinary verification
        verify(mockOne).add("one");

        //verify that method was never called on a mock
        verify(mockOne, never()).add("two");

        //verify that other mocks were not interacted
        verifyZeroInteractions(mockTwo, mockThree);
    }

    @Test
    public void testFindRedundantInvocations() {
        List mockedList = mock(List.class);

        //using mocks
        mockedList.add("one");
        mockedList.add("two");

        verify(mockedList).add("one");

        //following verification will fail
        verifyNoMoreInteractions(mockedList);
    }

    @Test
    public void testStubbingConsecutiveCalls() {
        when(mock.bar("some arg"))
                .thenThrow(new RuntimeException())
                .thenReturn("Mocked return");

        //First call: throws runtime exception:
        try {
            mock.bar("some arg");
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        //Second call: prints 1
        System.out.println(mock.bar("some arg"));

        //Any consecutive call: prints "1" as well (last stubbing wins).
        System.out.println(mock.bar("some arg"));

        // alternative shorter version
        when(mock.bar("some arg"))
                .thenReturn("one", "two", "three");
        System.out.println(mock.bar("some arg"));
        System.out.println(mock.bar("some arg"));
        System.out.println(mock.bar("some arg"));
    }

    @Test
    public void testStubbingWithCallbacks() {
        when(mock.bar(anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Object mock = invocation.getMock();
                return "called with arguments: " + args[0].toString();
            }
        });

        //the following prints "called with arguments: foo"
        System.out.println(mock.bar("foo"));
        System.out.println(mock.bar("xyz"));
    }

    @Test
    public void testStubbingVoidMethods() {
        List mockedList = mock(List.class);

        //  doReturn()|doThrow()| doAnswer()|doNothing()|doCallRealMethod() also available
        doThrow(new RuntimeException()).when(mockedList).clear();

        // following throws RuntimeException:
        mockedList.clear();
    }

    @Test
    public void testSpyingOnRealObjects() {
        List list = new LinkedList();
        // create a spy of real object
        List spy = spy(list);

        // When you use the spy then the real methods are called (unless a method was stubbed)
        //optionally, you can stub out some methods:
        when(spy.size()).thenReturn(100);

        //using the spy calls *real* methods
        spy.add("one");
        spy.add("two");

        //prints "one" - the first element of a list
        System.out.println(spy.get(0));

        //size() method was stubbed - 100 is printed
        System.out.println(spy.size());

        //optionally, you can verify
        verify(spy).add("one");
        verify(spy).add("two");

        // <!> Important gotcha on spying real objects! <!>
        //Impossible: real method is called so spy.get(0) throws IndexOutOfBoundsException (the list is yet empty)
        when(spy.get(0)).thenReturn("foo");

        //You have to use doReturn() for stubbing
        doReturn("foo").when(spy).get(0);
    }

    @Test
    public void testChangeDefaultReturnValues() {
        Clazz mock = mock(Clazz.class, Mockito.RETURNS_SMART_NULLS);
//        Foo mockTwo = mock(Foo.class, new YourOwnAnswer());s
//        Clazz mockTwo = mock(Clazz.class, new YourOwnAnswer());
        String a = mock.bar("alpha");
        System.out.println(a.length());
    }

    @Test
    public void testCapturingArguments() {
        // mockito verifies arguments by using equals() method
        // sometimes it's helpful to assert on certain arguments after the actual verification
        // ArgumentCaptor allows you to capture arguments which were passed to the method
        ArgumentCaptor<Person> argument = ArgumentCaptor.forClass(Person.class);
        Person vasja = new Person("Vasja","Pupkin",12);
        Person john = new Person("John","Walker",21);
        mock.xyz(vasja);
        mock.xyz(john);

        // some immediate check: test if xyz was called 2 times
        verify(mock,times(2)).xyz(argument.capture());

        // some more code ...
        // ...
        // ...

        // Now let's check which arguments were called on mock.xyz()
        assertEquals("Vasja",argument.getAllValues().get(0).getName());
        // getValue() always returns last call's arguments
        assertEquals("John", argument.getValue().getName());
    }


    // --- Custom Matchers ---

    class MyMatcher extends BaseMatcher<Integer> {

        @Override
        public boolean matches(Object item) {
            System.out.println("matching " + item.toString());
            boolean matches = item.equals("Vasja");
            System.out.println("item " + item.toString() + (matches ? " matches" : " does not match"));
            return matches;
        }

        @Override
        public void describeTo(Description description) {
            System.out.println(description.toString());
        }
    }

    // Preferred version as of Mockito 2+, to decouple from hamcrest
    class MyMatcher2 extends ArgumentMatcher<Integer> {

        @Override
        public boolean matches(Object argument) {
            System.out.println("matching " + argument.toString());
            boolean matches = argument.equals("Vasja");
            System.out.println("item " + argument.toString() + (matches ? " matches" : " does not match"));
            return matches;
        }
    }

    public static class Clazz {
        int a = 4;

        public void foo() {
            System.out.println("foo");
        }

        public void xyz(Person p){
            System.out.println("Doing xyz on "+p.toString());
        }

        public String bar(String str) {
            return "_" + str + "_";
        }
    }

}
