import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;



public class Main extends JarClassLoader {

	private static final Name BUNDLE_MAIN_CLASS = new Name("Bundle-MainClass");

	public Main() {
		super(Main.class.getClassLoader());
	}
	
	public static void main(String[] args) {
		Main jcl = new Main();
		try {
			jcl.invokeMain(args);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void invokeMain(String[] args) throws Throwable {
		String realMainClass = getBundleMainClassName();
		if (realMainClass == null) {
			System.out.println("main not found in manifest entry : " + BUNDLE_MAIN_CLASS);
			System.exit(1);
		}
		invokeMain(realMainClass, args);
	}

	private String getBundleMainClassName() {
		Attributes attr = null;
		if (isLaunchedFromJar()) {
			try {
				// The first element in array is the top level JAR
				Manifest m = lstJarFile.get(0).getManifest();
				attr = m.getMainAttributes();
			} catch (IOException e) {
			}
		}
		return (attr == null ? null : attr.getValue(BUNDLE_MAIN_CLASS));
	}
}