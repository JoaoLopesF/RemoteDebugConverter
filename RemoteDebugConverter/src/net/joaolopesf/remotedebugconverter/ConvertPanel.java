/*
 * RemoteDebugConverter - ConvertPanel
 */

package net.joaolopesf.remotedebugconverter;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.joaolopesf.util.ShowException;
import net.joaolopesf.util.UtilSwing;
import net.joaolopesf.util.Utilities;
import net.miginfocom.swing.MigLayout;

class ConvertPanel extends JPanel {

	// Version

	final String VERSION = "0.1.0";

	// Dimensions

	final int width = 600;
	final int height = 380;

	// Title

	public final String tittle = "Convert Arduino code to RemoteDebug - Converter version " + VERSION;

	// Variaveis

	private String directory = null;
	private String directoryConfig = null;

	private File fileSource = null;

	private int nrLinesIno = 0;
	private int posAddInclude = 0;
	private int totSerialPrints = 0;
	private int totSerialPrint = 0;
	private int totSerialPrintln = 0;
	private int totSerialPrintf = 0;
	private int totVarGlobals = 0;
	private int posLastInclude = 0;
	private int posSetup = 0;
	private int posEndSetup = 0;
	private int posLoop = 0;

	// Globals variables

	ArrayList<GlobalVariable> globals = new ArrayList<>();

	String[] varTypes = { "boolean", "char", "byte", "int", "unsigned int", "long", "unsigned long", "float", "double", "int8_t", "int16_t", "int32_t", "uint8_t", "uint16_t", "uint32_t", "String" };

	String[] varTypesSerialDebug = { "Boolean", "Char", "Byte", "Int", "UInt", "Long", "ULong", "Float", "Double", "Int8_t", "Int16_t", "Int32_t", "UInt8_t", "UInt16_t", "UInt32_t", "String" };
	// Font name

	String fontName = Utilities.getFontName(); // "Courier New";

	// Window

	private JPanel convertPanel;
	private JButton btnNext;
	private JLabel lblfile;
	private JLabel lblAnalyze;
	private JTabbedPane tabbedPane;
	private JButton btnAnalyze;
	private JLabel lblFileName;
	private JLabel lblTotalLines;
	private JLabel lblTotalGlobalVars;
	private JLabel lblTotSerialPrints;
	private JLabel lblfileConverter;
	private JLabel lblConversion;
	private JComboBox cmbAutoFunc;
	private JComboBox cmbDebugger;
	private JComboBox cmbUseFlash;

	/**
	 * Create the panel.
	 */

	public ConvertPanel() {

		// Panel

		convertPanel = this;

		setOpaque(false);
		setMinimumSize(new Dimension(width, height));
		setMaximumSize(getMinimumSize());
		setLayout(new BorderLayout(0, 0));

		JPanel panelButtons = new JPanel();
		panelButtons.setOpaque(false);
		add(panelButtons, BorderLayout.SOUTH);

		btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				next();
			}
		});
		panelButtons.add(btnNext);
		btnNext.setFont(new Font(fontName, Font.BOLD, 13));
		btnNext.setMargin(new Insets(10, 10, 10, 10));
		btnNext.setEnabled(false);

		JLabel label = new JLabel("     ");
		panelButtons.add(label);

		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnClose.setMargin(new Insets(10, 10, 10, 10));
		btnClose.setFont(new Font(fontName, Font.BOLD, 13));
		panelButtons.add(btnClose);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, BorderLayout.CENTER);

		JPanel panelOpen = new JPanel();
		tabbedPane.addTab("Open file", null, panelOpen, null);
		panelOpen.setLayout(null);

		JButton btnOpenInoFile = new JButton("Open Arduino souce file (ino)");
		btnOpenInoFile.setFont(new Font(fontName, Font.BOLD, 13));
		btnOpenInoFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				open();
			}
		});
		btnOpenInoFile.setBounds(159, 81, 268, 29);
		panelOpen.add(btnOpenInoFile);

		lblfile = new JLabel("");
		lblfile.setFont(new Font(fontName, Font.BOLD, 13));
		lblfile.setHorizontalAlignment(SwingConstants.CENTER);
		lblfile.setBounds(6, 126, 573, 16);
		panelOpen.add(lblfile);

		btnAnalyze = new JButton("Analyze the source code");
		btnAnalyze.setFont(new Font(fontName, Font.BOLD, 13));
		btnAnalyze.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				analyze();

			}
		});
		btnAnalyze.setBounds(159, 154, 266, 29);
		btnAnalyze.setVisible(false);
		panelOpen.add(btnAnalyze);

		lblAnalyze = new JLabel("");
		lblAnalyze.setHorizontalAlignment(SwingConstants.CENTER);
		lblAnalyze.setFont(new Font(fontName, Font.BOLD, 13));
		lblAnalyze.setBounds(6, 195, 573, 16);
		panelOpen.add(lblAnalyze);

		JPanel panelOptions = new JPanel();
		tabbedPane.addTab("Options", null, panelOptions, null);
		tabbedPane.setEnabledAt(1, false);
		panelOptions.setLayout(new MigLayout("", "[][grow]", "[][][][][][][][][][][][][]"));

		JLabel lblFile = new JLabel("File:");
		lblFile.setFont(new Font(fontName, Font.PLAIN, 13));
		panelOptions.add(lblFile, "cell 0 0,alignx right");

		lblFileName = new JLabel("...");
		lblFileName.setFont(new Font(fontName, Font.BOLD, 13));
		panelOptions.add(lblFileName, "cell 1 0");

		JLabel labelLines = new JLabel("Lines:");
		labelLines.setFont(new Font(fontName, Font.PLAIN, 13));
		panelOptions.add(labelLines, "flowx,cell 0 1,alignx right");

		lblTotalLines = new JLabel("0");
		lblTotalLines.setFont(new Font(fontName, Font.BOLD, 13));
		panelOptions.add(lblTotalLines, "cell 1 1,alignx left");

		JLabel lblGlobalVariablesDetected = new JLabel("Global variables detected:");
		lblGlobalVariablesDetected.setFont(new Font(fontName, Font.PLAIN, 13));
		panelOptions.add(lblGlobalVariablesDetected, "cell 0 2");

		lblTotalGlobalVars = new JLabel("0");
		lblTotalGlobalVars.setFont(new Font(fontName, Font.BOLD, 13));
		panelOptions.add(lblTotalGlobalVars, "flowx,cell 1 2");

		JLabel lblTotalOfSerialprint = new JLabel("Total of Serial.print*:");
		lblTotalOfSerialprint.setFont(new Font(fontName, Font.PLAIN, 13));
		panelOptions.add(lblTotalOfSerialprint, "cell 0 3,alignx right");

		lblTotSerialPrints = new JLabel("0");
		lblTotSerialPrints.setFont(new Font(fontName, Font.BOLD, 13));
		panelOptions.add(lblTotSerialPrints, "cell 1 3");

		JLabel lblNoteInThis = new JLabel("(Note: arrays is ignored. You can do it manually)");
		lblNoteInThis.setFont(new Font(fontName, Font.PLAIN, 13));
		panelOptions.add(lblNoteInThis, "cell 1 2");

		JLabel labelAutoFunc = new JLabel("Auto function on debug:\n");
		labelAutoFunc.setHorizontalAlignment(SwingConstants.TRAILING);
		labelAutoFunc.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		panelOptions.add(labelAutoFunc, "cell 0 5,alignx trailing");

		cmbAutoFunc = new JComboBox();
		cmbAutoFunc.setModel(new DefaultComboBoxModel(new String[] { "Yes", "No" }));
		cmbAutoFunc.setFont(new Font(fontName, Font.BOLD, 13));
		panelOptions.add(cmbAutoFunc, "cell 1 5,growx");

		JLabel lblDebuugerEnabled = new JLabel("Add RemoteDebugger ?");
		lblDebuugerEnabled.setHorizontalAlignment(SwingConstants.TRAILING);
		lblDebuugerEnabled.setFont(new Font(fontName, Font.PLAIN, 13));
		panelOptions.add(lblDebuugerEnabled, "cell 0 6,alignx trailing");

		cmbDebugger = new JComboBox();
		cmbDebugger.setEnabled(false);
		cmbDebugger.setModel(new DefaultComboBoxModel(new String[] { "Yes", "No" }));
		cmbDebugger.setSelectedIndex(1);
		cmbDebugger.setFont(new Font(fontName, Font.BOLD, 13));
		panelOptions.add(cmbDebugger, "cell 1 6,growx");

		JLabel lblNoteIiThis = new JLabel("<html>\nNote: this version not yet add debugger codes. <br>\nUse RemoteDebuggerConverter for it.\n</html>");
		lblNoteIiThis.setFont(new Font("Dialog", Font.BOLD, 10));
		panelOptions.add(lblNoteIiThis, "cell 1 7");

		JLabel lblUseFlashF = new JLabel("Use flash F() for debugger?:");
		lblUseFlashF.setHorizontalAlignment(SwingConstants.TRAILING);
		lblUseFlashF.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		panelOptions.add(lblUseFlashF, "cell 0 8,alignx trailing");

		cmbUseFlash = new JComboBox();
		cmbUseFlash.setEnabled(false);
		cmbUseFlash.setModel(new DefaultComboBoxModel(new String[] { "Yes", "No" }));
		cmbUseFlash.setSelectedIndex(1);
		cmbUseFlash.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		panelOptions.add(cmbUseFlash, "cell 1 8,growx");

		JLabel lblNoteSetIt = new JLabel("Note: set it if really need save memory (it is slower than RAM).");
		lblNoteSetIt.setFont(new Font("Dialog", Font.BOLD, 10));
		panelOptions.add(lblNoteSetIt, "cell 1 9");

		JLabel lblClickInNext = new JLabel("Click in Next to convert");
		lblClickInNext.setFont(new Font(fontName, Font.BOLD, 13));
		panelOptions.add(lblClickInNext, "cell 1 12");

		JPanel panelConvert = new JPanel();
		tabbedPane.addTab("Convert", null, panelConvert, null);
		panelConvert.setLayout(null);

		JButton btnConvert = new JButton("Click to convert code");
		btnConvert.setFont(new Font(fontName, Font.BOLD, 13));
		btnConvert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				convert();
			}
		});
		btnConvert.setBounds(182, 116, 228, 29);
		panelConvert.add(btnConvert);

		lblfileConverter = new JLabel("");
		lblfileConverter.setHorizontalAlignment(SwingConstants.CENTER);
		lblfileConverter.setFont(new Font(fontName, Font.BOLD, 13));
		lblfileConverter.setBounds(6, 63, 573, 16);
		panelConvert.add(lblfileConverter);

		lblConversion = new JLabel("");
		lblConversion.setHorizontalAlignment(SwingConstants.CENTER);
		lblConversion.setFont(new Font(fontName, Font.BOLD, 13));
		lblConversion.setBounds(6, 179, 573, 16);
		panelConvert.add(lblConversion);

		tabbedPane.setEnabledAt(1, false);
		tabbedPane.setEnabledAt(2, false);

		// Read properties

		readProperties();

	}

	// Read properties

	void readProperties() {

		try {

			Properties props = new Properties();
			InputStream inputStream = null;

			// Directory of S.O.

			if (Utilities.isMacOS() || Utilities.isUnix()) {

				// MacOsx and Linux

				directoryConfig = System.getProperty("user.home") + File.separator + "/.remotedebugconverter";

			} else if (Utilities.isWindows()) {

				directoryConfig = System.getenv("APPDATA") + File.separator + "RemoteDebugConverter";

			} else { // Other ????

				return;
			}

			File fileDirectory = new File(directoryConfig);

			if (!fileDirectory.exists()) {
				fileDirectory.mkdirs();
			}

			File file = new File(directoryConfig + File.separator + "RemoteDebug_Convert.properties");

			if (file.exists()) {

				inputStream = new FileInputStream(file);
				props.load(inputStream);

				directory = props.getProperty("directory", "");

				inputStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			ShowException.show(e);
		}
	}

	// Save properties

	void saveProperties() {

		try {

			// Set

			Properties props = new Properties();

			props.setProperty("directory", (directory != null) ? directory : "");

			// Save

			File file = new File(directoryConfig + File.separator + "RemoteDebug_Convert.properties");
			OutputStream out = new FileOutputStream(file);
			props.store(out, "RemoteDebug_Convert");
			out.close();

		} catch (Exception e) {
			ShowException.show(e);
		}
	}

	// Next tab

	void next() {

		int actual = tabbedPane.getSelectedIndex();

		actual++;
		tabbedPane.setSelectedIndex(actual);

		if ((actual + 1) == tabbedPane.getComponentCount()) {
			btnNext.setEnabled(false);
		}

	}

	// Open source file

	void open() {

		try {

			final JFileChooser fcfile = new JFileChooser();

			try {
				fcfile.setCurrentDirectory(new File(directory));
			} catch (Exception e) {
			}
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Arduino source files (*.ino)", "ino");
			fcfile.setFileFilter(filter);
			fcfile.setDialogTitle("Open source file Arduino");
			int returnVal = fcfile.showOpenDialog(convertPanel);

			// Selected ?

			if (returnVal != JFileChooser.APPROVE_OPTION) {
				return;
			}

			// Save file

			fileSource = fcfile.getSelectedFile();

			directory = fileSource.getParent();

			saveProperties();

			tabbedPane.setEnabledAt(1, false);
			tabbedPane.setEnabledAt(2, false);

			String path = fileSource.getPath();
			if (path.length() > 50) {
				path = "..." + path.substring((path.length() - 50));
			}

			lblfile.setText(path);
			lblfileConverter.setText(path);
			lblConversion.setText("Note: A directory with _RDbg suffix will be created to save the converted code.");

			btnAnalyze.setVisible(true);

			lblAnalyze.setText("");
			btnNext.setEnabled(false);

		} catch (Exception e) {
			ShowException.show(e);
		}
	}

	// Analyze

	void analyze() {

		BufferedReader bufferedReader = null;
		FileReader fileReader = null;

		try {

			lblAnalyze.setText("Analyzing the source code ...");

			// Variables

			nrLinesIno = 0;
			posAddInclude = 0;
			totSerialPrints = 0;
			totSerialPrint = 0;
			totSerialPrintln = 0;
			totSerialPrintf = 0;
			totVarGlobals = 0;
			posLastInclude = 0;
			posSetup = 0;
			posEndSetup = 0;
			posLoop = 0;

			String line = "";
			int lineNumber = 0;
			int countBrackets = 0;
			boolean inComment = false;
			boolean inPreCompiler = false;
			boolean ignore = false;
			int lastLineUtil = 0;

			globals.clear();

			// Open file

			fileReader = new FileReader(fileSource);

			bufferedReader = new BufferedReader(fileReader);

			// Process

			do {

				// Read line

				line = bufferedReader.readLine();

				if (line != null) {

					lineNumber++;

					// System.out.println("line -> " + line);
					line = line.trim();

					line = line.replace("\t", " ");
					line = line.replace("  ", " ");
					line = line.replace("  ", " ");

					line = line.replace("static ", "");

					System.out.println("line -> " + line);

					ignore = false;

					// In comments ?

					if (inComment) {
						if (line.startsWith("*/")) {
							inComment = false;
						}
						ignore = true;
					} else if (line.startsWith("//")) {
						ignore = true;
					} else if (line.startsWith("/*")) {
						inComment = true;
						ignore = true;
					}

					// In precompiler ?

					if (line.startsWith("#if")) {
						inPreCompiler = true;
					}
					if (line.startsWith("#endif")) {
						inPreCompiler = false;
					}
					if (inPreCompiler) {
						ignore = true;
					}

					if (ignore && line.contains("#include")) {
						ignore = false; // the #include can be in #if/#endif
					}

					// Only if not ignored

					if (!ignore) {

						// Count brackets

						if (line.contains("{")) {
							countBrackets++;
						}
						if (line.contains("}")) {
							countBrackets--;
						}

						// Code before setup ?

						if (posSetup == 0) {

							if (line.contains("#include")) { // Process includes

								if (line.contains("RemoteDebug.h")) {

									lblAnalyze.setText("This source file already using RemoteDebug Library");
									btnNext.setEnabled(false);
									return;
								}

								// Position last include

								if (!inPreCompiler && countBrackets == 0) {
									posLastInclude = lineNumber + 1;
									System.out.println("posInclude = " + posLastInclude);
								}
							}

							// Process global variables

							int posType = 0;

							for (String type : varTypes) {

								if (line.startsWith(type + " ") && !(line.contains("["))) { // Not for arrays

									String aux = line.replace(" ", ":").replace(";", "").replace(",", ":") + ':'; // Todo:inta,b,c

									String fields[] = aux.split(":");

									int posName = (type.startsWith("unsigned") ? 2 : 1);

									if (fields.length >= 2) {

										totVarGlobals++;

										GlobalVariable variable = new GlobalVariable();
										variable.setNum(totVarGlobals);
										variable.setName(fields[posName]);
										variable.setType(type);
										variable.setPosType(posType);
										System.out.println("variable: name=" + fields[1] + " type=" + type);

										globals.add(variable);
									}
								}

								posType++;
							}

							// Setup

							if (line.startsWith("void setup()") || line.startsWith("void setup ()")) {

								posSetup = lineNumber;

								posLastInclude = lastLineUtil; // lineNumber;

							}

						} else { // Processing functions

							if (lineNumber == (posSetup + 1) && line.startsWith("{")) { // Adjusts it
								posSetup = lineNumber;
							}

							if (posSetup > 0 && posEndSetup == 0 && lineNumber > posSetup && countBrackets == 0 && line.contains("}")) { // End of setup code

								posEndSetup = lineNumber - 1;
							}

							if (line.startsWith("void loop()") || line.startsWith("void loop ()")) {
								posLoop = lineNumber;
							}
							if (posLoop > 0 && lineNumber == (posLoop + 1) && line.startsWith("{")) { // Adjusts it
								posLoop = lineNumber;
							}

							if (line.startsWith("Serial.print")) { // count serial.prints

								if (!line.contentEquals("Serial.print(\".\");")) {

									totSerialPrints++;

									System.out.println("tot prints -> " + totSerialPrints);

									totSerialPrint++;

									System.out.println("tot print -> " + totSerialPrint);

									if (line.startsWith("Serial.println")) { // count serial.prints

										totSerialPrintln++;

										System.out.println("tot println -> " + totSerialPrintln);
									}
									if (line.startsWith("Serial.printf")) { // count serial.prints

										totSerialPrintf++;

										System.out.println("tot printf -> " + totSerialPrintf);
									}
								}
							}
						}

						// Last position util

						if (line.length() > 0) {
							lastLineUtil = lineNumber;
						}

					}

				}

			} while (line != null);

			// Save

			nrLinesIno = lineNumber;

			lblAnalyze.setText("File analyzed with success. Click in next");
			btnNext.setEnabled(true);

			lblFileName.setText(fileSource.getName());
			lblTotalLines.setText("" + nrLinesIno);
			lblTotalGlobalVars.setText("" + totVarGlobals);
			lblTotSerialPrints.setText("" + totSerialPrints);

			btnNext.setEnabled(true);

			if (totSerialPrint > 0) {
				cmbAutoFunc.setSelectedIndex(1);
				cmbAutoFunc.setEnabled(false);
			} else {
				cmbAutoFunc.setSelectedIndex(0);
				cmbAutoFunc.setEnabled(true);
			}
		} catch (Exception e) {
			ShowException.show(e);
		} finally {

			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}

				if (fileReader != null) {
					fileReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Copy file

	void copyFile(String source, String destiny) throws IOException {

		CopyOption[] options = new CopyOption[] { StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES };

		Path pathSource = Paths.get(source);
		Path pathDestiny = Paths.get(destiny);

		Files.copy(pathSource, pathDestiny, StandardCopyOption.REPLACE_EXISTING);

	}

	// Convert

	protected void convert() {

		BufferedReader bufferedReader = null;
		FileReader fileReader = null;

		FileWriter fileWriter = null;
		PrintWriter printWriter = null;

		try {

			lblConversion.setText("Converting the source code ...");

			// Variables

			String line = "";
			int lineNumber = 0;

			// Create directory of destiny

			File fileDir = fileSource.getParentFile();

			String dirDestiny = fileDir.getParentFile().getPath() + File.separator + fileDir.getName() + "_RDbg";
			File fileDirDestiny = new File(dirDestiny);

			if (fileDirDestiny.exists()) {

				lblConversion.setText("Unable to convert. Directory already exists: " + fileDir.getName() + "_RDbg");
				return;

			} else {

				fileDirDestiny.mkdir();

			}

			// Copy the files, except the ino

			File[] fList = fileDir.listFiles(); // Visto em https://dzone.com/articles/java-example-list-all-files

			for (File file : fList) {

				if (!(file.isDirectory())) {

					if (!(file.getName().endsWith(".ino"))) {
						System.out.println("copying " + file.getName());
						copyFile(file.getPath(), dirDestiny + File.separator + file.getName());
					}
				}
			}

			// Open the source file

			fileReader = new FileReader(fileSource);

			bufferedReader = new BufferedReader(fileReader);

			// Open the file of destiny

			String pathDestiny = dirDestiny + File.separator + fileSource.getName();
			pathDestiny = pathDestiny.replace(".ino", "_RDbg.ino");
			fileWriter = new FileWriter(pathDestiny);
			printWriter = new PrintWriter(fileWriter);

			// Process file

			do {

				// Read a line

				line = bufferedReader.readLine();

				if (line != null) {

					lineNumber++;

					// Convert Serial.prints

					if (line.startsWith("//")) {

						// ignore comments

					} else if (line.contains("Serial.print") && (!line.contentEquals("Serial.print(\".\");"))) {

						if (lineNumber >= posSetup && lineNumber <= posEndSetup) { // In setup ? -> debug level always

							// Not convert

						} else {

							// Note using rprint* macros to convert, until can convert it to one printf (debug*)

							line = line.replace("Serial.printf", "rdebugD").replace("Serial.println", "rprintDln").replace("Serial.print", "rprintD");
						}
					}

					// Write line

					printWriter.println(line);

					// Process

//					if (lineNumber == (posSetup - 1)) {
//					if (lineNumber == posSetup) {
					if (lineNumber == posLastInclude) {

						// Generare include and options

						printWriter.println("");
						printWriter.println("// Remote debug over telnet - not recommended for production/release, only for development");
						printWriter.println("");
						printWriter.println("// Disable all debug ?");
						printWriter.println("");
						printWriter.println("// Important to compile for prodution/release");
						printWriter.println("// Disable all debug ? Good to release builds (production)");
						printWriter.println("// as nothing of RemoteDebug is compiled, zero overhead :-)");
						printWriter.println("// For it just uncomment the DEBUG_DISABLED");
						printWriter.println("// On change it, if in another IDE than Arduino IDE, like Eclipse or VSCODE,");
						printWriter.println("// please clean the project, before compile");
						printWriter.println("");
						printWriter.println("//#define DEBUG_DISABLED true");
						printWriter.println("");
						printWriter.println("// Disable the auto function feature of RemoteDebug ?");
						printWriter.println("");
						printWriter.println("//#define DEBUG_DISABLE_AUTO_FUNC true");
						printWriter.println("");

						printWriter.println("#include \"RemoteDebug.h\"        //https://github.com/JoaoLopesF/RemoteDebug");
						printWriter.println("");
						printWriter.println("#ifndef DEBUG_DISABLED // Only if debug is not disabled (for production/release)");
						printWriter.println("");
						printWriter.println("// Instance of RemoteDebug");
						printWriter.println("");
						printWriter.println("RemoteDebug Debug;");
						printWriter.println("");
						printWriter.println("#endif");
						printWriter.println("");
						printWriter.println("// This code was been converted by RemoteDebugConverter");
						printWriter.println("");

					} else if (lineNumber == posEndSetup) {

						// Add RemoteDebug init codes

						printWriter.println("");
						printWriter.println("#ifndef DEBUG_DISABLED // Only for development");
						printWriter.println("");
						printWriter.println("    // Initialize the WiFi server of RemoteDebug");
						printWriter.println("");
						printWriter.println("    Debug.begin(\"\"); // Initiaze the WiFi server (please put here the hostname)");
						printWriter.println("");
						printWriter.println("    //Debug.setPassword(\"r3m0t0.\"); // Password of WiFi (as telnet) connection ?");
						printWriter.println("");
						printWriter.println("    Debug.setResetCmdEnabled(true); // Enable the reset command");
						printWriter.println("");
						printWriter.println("    Debug.showProfiler(true); // Profiler (Good to measure times, to optimize codes)");
						printWriter.println("");
						printWriter.println("    Debug.showColors(true); // Colors");
						printWriter.println("");
						printWriter.println("    //Debug.setSerialEnabled(true); // if you wants serial echo - only recommended if ESP is plugged in USB");
						printWriter.println("");
						printWriter.println("#endif");
						printWriter.println("");

					} else if (lineNumber == posLoop) {

						// Generate RemoteDebug handle"

						printWriter.println("");
						printWriter.println("#ifndef DEBUG_DISABLED");
						printWriter.println("");
						printWriter.println("    // RemoteDebug handle (for WiFi connections)");
						printWriter.println("");
						printWriter.println("    Debug.handle();");
						printWriter.println("");
						printWriter.println("");
						printWriter.println("#endif");
					}

				}

			} while (line != null);

			// Fim

			lblConversion.setText("File converted with success.");

			// Open the folder in S.O

			Desktop.getDesktop().open(fileDirDestiny);

		} catch (Exception e) {
			ShowException.show(e);
		} finally {

			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}

				if (fileReader != null) {
					fileReader.close();
				}

				if (printWriter != null) {
					printWriter.close();
				}

				if (fileWriter != null) {
					fileWriter.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}

// Class to store global variables

class GlobalVariable {

	int num = 0;
	String name = "";
	String type = "";
	int posType = 0;
	String description = "";
	boolean gerar = true;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isGerar() {
		return gerar;
	}

	public void setGerar(boolean gerar) {
		this.gerar = gerar;
	}

	public int getPosType() {
		return posType;
	}

	public void setPosType(int posType) {
		this.posType = posType;
	}
}
