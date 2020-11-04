package recortador;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Main {

	public static void setLectura(String[] lectura) {
		Main.lectura = lectura;
	}

	static LinkedList<String> listaImagenes = new LinkedList<>();
	static String directorioImagenes;
	static String directorioActual = "";

	public static String[] getSonido() {
		return sonido;
	}

	public static void setSonido(String[] sonido) {
		Main.sonido = sonido;
	}

	static String[] sonido = Metodos.leerFicheroArray("Config/sonido.txt", 2);

	static String os = System.getProperty("os.name");

	private static String[] lectura = Metodos.leerFicheroArray("Config/Config.txt", 2);

	public static String[] getLectura() {
		return lectura;
	}

	public static String getOs() {
		return os;
	}

	public static String getSeparador() {
		return separador;
	}

	static String separador = Metodos.saberSeparador(os);

	public static String getDirectorioActual() {
		return directorioActual;
	}

	private static void subida() {

		try {

			if (!lectura[0].isEmpty() && lectura[0] != null) {

				WebDriver chrome = new ChromeDriver();

				chrome.get(lectura[1] + "/Hacer_gif/crear_gif.php");

				chrome.close();

				Metodos.cerrarNavegador();

				listaImagenes = Metodos.directorio(lectura[0] + separador + "Hacer_gif" + separador + "Output", "gif");

				LinkedList<String> frames = Metodos.directorio(lectura[0] + separador + "Hacer_gif" + separador + "img",
						".");

				for (int x = 0; x < listaImagenes.size(); x++) {

					Files.move(
							FileSystems.getDefault()
									.getPath(lectura[0] + separador + "Hacer_gif" + separador + "Output" + separador
											+ listaImagenes.get(x)),
							FileSystems.getDefault().getPath(directorioImagenes + separador + listaImagenes.get(x)),
							StandardCopyOption.REPLACE_EXISTING);
				}

				for (int x = 0; x < frames.size(); x++) {
					Metodos.eliminarFichero(
							lectura[0] + separador + "Hacer_gif" + separador + "img" + separador + frames.get(x));
				}

				Metodos.abrirCarpeta(directorioImagenes);

			}

		}

		catch (ArrayIndexOutOfBoundsException e1) {

		}

		catch (Exception e1) {

			try {
				hacerGIF();
			}

			catch (IOException e) {
				//
			}
		}

	}

	public static void hacerGIF() throws IOException {

		try {

			if (Metodos.listarFicherosPorCarpeta(new File(lectura[0] + "/Hacer_gif/img"), ".") <= 1) {
				Metodos.mensaje("Tienes que tener al menos 2 imágenes para crear un GIF", 3);
				Metodos.abrirCarpeta(lectura[0] + separador + "Hacer_gif" + separador + "img" + separador);
			}

			else {

				if (Metodos.listarFicherosPorCarpeta(new File(lectura[0] + "/Hacer_gif/img"), ".") > 170) {
					Metodos.mensaje("Has superado el límite de imágenes para crear un GIF", 3);
					Metodos.abrirCarpeta(lectura[0] + separador + "Hacer_gif" + separador + "img" + separador);
				}

				else {

					File af = new File("Config/Config.txt");

					if (af.exists()) {
						subida();
					}
				}
			}
		}

		catch (Exception e) {

		}
	}

	public static void mensaje170() {

		Metodos.mensaje("Tienes más de 170 imágenes", 3);

		try {
			Metodos.abrirCarpeta(lectura[0] + separador + "Hacer_gif" + separador + "img");
		}

		catch (IOException e1) {

		}

	}

	public static void main(String[] args) {

		Metodos.crearCarpetas();

		LinkedList<String> listaImagenes = new LinkedList<>();

		try {
			directorioActual = new File(".").getCanonicalPath() + separador;
			directorioImagenes = directorioActual + "Config" + separador + "imagenes";
		} catch (IOException e) {
			//
		}
		try {

			listaImagenes = Metodos.directorio(directorioActual + "Config" + separador + "imagenes_para_recortar", ".");

			if (!listaImagenes.isEmpty()) {

				Metodos.renombrarArchivos(listaImagenes,
						directorioActual + "Config" + separador + "imagenes_para_recortar" + separador);
			}

			new PhotoFrame().setVisible(true);
		}

		catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
