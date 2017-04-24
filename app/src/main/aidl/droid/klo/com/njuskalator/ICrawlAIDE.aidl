// ICrawlAIDE.aidl
package droid.klo.com.njuskalator;

// Declare any non-default types here with import statements

interface ICrawlAIDE {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    void updateServer(String s);
        //DEBUG
    void testService();
}
