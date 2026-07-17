package org.example.util;

public sealed interface Value permits NumberValue, StringValue, RangeValue, ErrorValue {

}
