package com.shau.mocap.parser.c3d;

import com.shau.mocap.exception.ParserException;
import com.shau.mocap.parser.c3d.domain.C3dGroup;
import com.shau.mocap.parser.c3d.domain.C3dParameter;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteOrder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class C3dParameterHelperTest {

    private Map<Integer, C3dGroup> groupParameters;

    @Before
    public void initTests() {

        groupParameters = new HashMap<>();

        C3dGroup group1 = new C3dGroup(1, "group1", "group1");
        group1.addParameter(new C3dParameter("parameter1",
                "parameter1",
                1,
                false,
                1,
                new int[]{1},
                Collections.EMPTY_LIST));
        group1.addParameter(new C3dParameter("parameter2",
                "parameter2",
                1,
                false,
                1,
                new int[]{1},
                Collections.EMPTY_LIST));

        C3dGroup group2 = new C3dGroup(2, "group2", "group2");
        group2.addParameter(new C3dParameter("parameter3",
                "parameter3",
                2,
                false,
                1,
                new int[]{1},
                Collections.EMPTY_LIST));
        group2.addParameter(new C3dParameter("parameter4",
                "parameter4",
                2,
                true,
                1,
                new int[]{1},
                Collections.EMPTY_LIST));

        groupParameters.put(1, group1);
        groupParameters.put(2, group2);
    }

    @Test
    public void testCreateParameterDataSuccess() throws Exception {

        //character
        byte[] data = {97};
        Object result = C3dParameterHelper.create(-1, data, ByteOrder.LITTLE_ENDIAN);
        assertThat(result, instanceOf(Character.class));
        assertThat((Character) result, is(Character.valueOf('a')));

        //integer small
        data = new byte[]{100};
        result = C3dParameterHelper.create(1, data, ByteOrder.LITTLE_ENDIAN);
        assertThat(result, instanceOf(Integer.class));
        assertThat((Integer) result, is(100));

        //integer large
        data = new byte[]{44, 1};
        result = C3dParameterHelper.create(2, data, ByteOrder.LITTLE_ENDIAN);
        assertThat(result, instanceOf(Integer.class));
        assertThat((Integer) result, is(300));

        //float
        data = new byte[]{0, 0, 0, 0};
        result = C3dParameterHelper.create(4, data, ByteOrder.LITTLE_ENDIAN);
        assertThat(result, instanceOf(Float.class));
        assertThat((Float) result, is(0.0f));
    }

    @Test(expected = ParserException.class)
    public void testCreateParameterWithDataInvalidType() throws Exception {
        byte[] data = new byte[]{100};
        Object result = C3dParameterHelper.create(10, data, ByteOrder.LITTLE_ENDIAN);
    }

    @Test(expected = ParserException.class)
    public void testCreateParameterWithDataInvalidData() throws Exception {
        byte[] data = new byte[]{0, 0};
        Object result = C3dParameterHelper.create(4, data, ByteOrder.LITTLE_ENDIAN);
    }

    @Test
    public void testParameterSizeSuccess() throws Exception {

        //character
        int size = C3dParameterHelper.parameterSize(-1);
        assertThat(size, is(1));

        //integer small
        size = C3dParameterHelper.parameterSize(1);
        assertThat(size, is(1));

        //integer large
        size = C3dParameterHelper.parameterSize(2);
        assertThat(size, is(2));

        //float
        size = C3dParameterHelper.parameterSize(4);
        assertThat(size, is(4));
    }

    @Test(expected = ParserException.class)
    public void testParameterSizeWithInvalidType() throws Exception {
        C3dParameterHelper.parameterSize(10);
    }

    @Test
    public void testGetGroupParameterSuccess() throws Exception {

        C3dParameter parameter = C3dParameterHelper.getParameter("group1",
                "parameter1",
                groupParameters);
        assertThat(parameter, is(notNullValue()));
        assertThat(parameter.getName(), is("parameter1"));

        parameter = C3dParameterHelper.getParameter("group1",
                "parameter2",
                groupParameters);
        assertThat(parameter, is(notNullValue()));
        assertThat(parameter.getName(), is("parameter2"));

        parameter = C3dParameterHelper.getParameter("group2",
                "parameter3",
                groupParameters);
        assertThat(parameter, is(notNullValue()));
        assertThat(parameter.getName(), is("parameter3"));
    }

    @Test(expected = ParserException.class)
    public void testGetGroupParameterGroupNotFound() throws Exception {
        C3dParameter parameter = C3dParameterHelper.getParameter("groupX",
                "parameter1",
                groupParameters);
    }

    @Test(expected = ParserException.class)
    public void testGetGroupParameterParameterNotFound() throws Exception {
        C3dParameter parameter = C3dParameterHelper.getParameter("group1",
                "parameterX",
                groupParameters);
    }

    @Test(expected = ParserException.class)
    public void testGetGroupParameterParameterLocked() throws Exception {
        C3dParameter parameter = C3dParameterHelper.getParameter("group2",
                "parameter4",
                groupParameters);
    }
}