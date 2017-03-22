package com.example.android.testing.unittesting.BasicSample;


import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;


public class SampleMockitoTest {

    @Test
    public void TestMockedList(){
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
    public void testMockedListStubbing(){
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
    public void testArgumentMatchersDemo(){
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
        boolean listContaintsMyName=mockedList.contains("Vasja");
        boolean listContainsAlexey=mockedList.contains("Alexey");
        //following prints "element"
        System.out.println(mockedList.get(999));

        //you can also verify using an argument matcher
        // verify that mockedList's get() method was called with some int type 3 times
        verify(mockedList,times(3)).get(anyInt());
        // did Vasja search for his name in the list?
        verify(mockedList).contains("Vasja");

        //argument matchers can also be written as Java 8 Lambdas
        // check if a string with length bigger than 5 was called
//        verify(mockedList).add(argThat(someString -> someString.length() > 5));

    }

    @Test
    public void testExactNumberOfInvocations(){
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
//        verify(mockedList, atLeast(2)).add("five times");
//        verify(mockedList, atMost(5)).add("three times");
    }




    // --- Custom Matchers ---

    class MyMatcher extends BaseMatcher<Integer>{

        @Override
        public boolean matches(Object item) {
            System.out.println("matching "+item.toString());
            boolean matches = item.equals("Vasja");
            System.out.println("item "+item.toString()+(matches?" matches":" does not match"));
            return matches;
        }

        @Override
        public void describeTo(Description description) {
            System.out.println(description.toString());
        }
    }

    // Preferred version as of Mockito 2+, to decouple from hamcrest
    class MyMatcher2 extends ArgumentMatcher<Integer>{

        @Override
        public boolean matches(Object argument) {
            System.out.println("matching "+argument.toString());
            boolean matches = argument.equals("Vasja");
            System.out.println("item "+argument.toString()+(matches?" matches":" does not match"));
            return matches;
        }
    }
}
