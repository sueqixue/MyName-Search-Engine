/*	CS1660 Course Project
 *  Qi Xue - qix22@pitt.edu
 *  University of Pittsburgh */

import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;
import java.time.Duration;
import java.time.Instant;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.*;

import com.google.cloud.dataproc.v1.HadoopJob;
import com.google.cloud.dataproc.v1.Job;
import com.google.cloud.dataproc.v1.JobControllerClient;
import com.google.cloud.dataproc.v1.JobControllerSettings;
import com.google.cloud.dataproc.v1.JobPlacement;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class searchEngine extends JFrame implements ActionListener {
	Container contentPane;
	JTextArea textArea = new JTextArea("Load My Engine");
	JTextArea filesNameDisplay = new JTextArea("");
	JFileChooser fc = new JFileChooser();
	JButton chooseFilesButton;
	JButton constructButton;
	JButton searchTermButton;
	JButton topNButton;
	JButton termSearchButton;
	JButton topNSearchButton;
	JButton goBackSearchButton;
	JTextField termField;
	JTextField topNField;
	String storedTerm;
	String storedN;
	JScrollPane jsp;

	public searchEngine() {
		contentPane = this.getContentPane();
		contentPane.setBackground(Color.WHITE);
		this.setTitle("MyName Search Engine");
		this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setResizable(false);
		this.setSize(1000, 640);
		
		// Initial All Buttons
		textAreaInit();
		filesNameDisplayInit();
		chooseFilesButtonInit();
		constructButtonInit();
		searchTermButtonInit();
		topNButtonInit();
		termFieldInit();
		termSearchButtonInit();
		topNFieldInit();
		topNSearchButtonInit();
		goBackSearchButtonInit();
		
		// Add Action Listener to Buttons
		chooseFilesButton.addActionListener(this);
		constructButton.addActionListener(this);
		searchTermButton.addActionListener(this);
		topNButton.addActionListener(this);
		termSearchButton.addActionListener(this);
		topNSearchButton.addActionListener(this);
		goBackSearchButton.addActionListener(this);
		
		// Add Components to contentPane
		contentPane.add(textArea);
		contentPane.add(filesNameDisplay);
        contentPane.add(chooseFilesButton);
        contentPane.add(constructButton);
        contentPane.add(searchTermButton);
        contentPane.add(topNButton);
        contentPane.add(termField);
        contentPane.add(termSearchButton);
        contentPane.add(topNField);
        contentPane.add(topNSearchButton);
        contentPane.add(goBackSearchButton);
	}
	

	public void actionPerformed(ActionEvent e) {
	    JButton button = (JButton)e.getSource();

	    if(button == chooseFilesButton) { // Click on chooseFilesButton
	    	System.out.println("\nChoosing Files:");
	    	getFileChooserPage();
	    }
	    else if(button == constructButton) { // Click on constructButton
	    	System.out.println("\nSelect Action:");
	    		    	
	    	try { // Submit Job
				submitJobToGCP();
			} catch (InterruptedException e1) {
				System.err.println(e1.getMessage());
				System.exit(1);
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}
	    		    	
	    	getSuccessfulPage();
	    }
	    else if(button == searchTermButton) { // Click on searchTermButton
	    	System.out.println("\n -- Search for a Term -- ");
	    	getSearchTermPage();
	    }
	    else if(button == topNButton) { // Click on topNButton
	    	System.out.println("\n -- Search for Top-N -- ");
	    	getSearchTopNPage();	    	
	    }
	    else if(button == termSearchButton && !termField.getText().isEmpty() && !termField.getText().equals("Type Your Search Here ...")) { // Click on termSearchButton
	    	storedTerm = termField.getText();
	    	System.out.println("\nDisplay search results for " + storedTerm);
	    	getTermResultPage(storedTerm);
	    }
	    else if(button == topNSearchButton && !topNField.getText().isEmpty() && !topNField.getText().equals("Type Your N Here ...")) { // Click on topNSearchButton
	    	storedN = topNField.getText();
	    	System.out.println("\nDisplaying search results for top-" + storedN);
	    	getTopNResultPage(storedN);
	    }
	    else if (button == goBackSearchButton) { // Click on goBackSearchButton	 
	    	jsp.setVisible(false);
	    	getSuccessfulPage();
	    }
    }
	
	void textAreaInit() {
		textArea.setFont(new Font("Times", Font.BOLD, 25));
        textArea.setEditable(false);
        textArea.setBounds(393, 160, 300, 30);
	}
	
	void filesNameDisplayInit() {
		filesNameDisplay.setFont(new Font("Times", Font.BOLD, 15));
		filesNameDisplay.setEditable(false);
        filesNameDisplay.setBounds(420, 310, 300, 70);
	}
        
	void chooseFilesButtonInit() {
		chooseFilesButton = new JButton("Choose Files");
		chooseFilesButton.setFont(new Font("Times", Font.BOLD, 15));
		chooseFilesButton.setBounds(400, 250, 200, 50);
	}
	
	void constructButtonInit() {
		constructButton = new JButton("Construct Inverted Indicies");
		constructButton.setFont(new Font("Times", Font.BOLD, 15));
		constructButton.setBounds(350, 410, 300, 80);
	}
	
	void searchTermButtonInit() {
		searchTermButton = new JButton("Search for Term");
		searchTermButton.setFont(new Font("Times", Font.BOLD, 15));
		searchTermButton.setBounds(400, 360, 200, 50);
		searchTermButton.setVisible(false);
	}
	
	void topNButtonInit() {
		topNButton = new JButton("TOP-N");
		topNButton.setFont(new Font("Times", Font.BOLD, 15));
		topNButton.setBounds(400, 450, 200, 50);
		topNButton.setVisible(false);
	}
	
	void termFieldInit() {
		termField = new JTextField();
		termField.setBounds(400, 250, 200, 50);
		termField.setVisible(false);
		termField.setForeground(Color.GRAY);
	}
	
	void termSearchButtonInit() {
		termSearchButton = new JButton("Search");
		termSearchButton.setFont(new Font("Times", Font.BOLD, 15));
		termSearchButton.setBounds(400, 400, 200, 50);
		termSearchButton.setVisible(false);
	}
	
	void topNFieldInit() {
		topNField = new JTextField("Type Your N Here ...");
		topNField.setBounds(400, 250, 200, 50);
		topNField.setVisible(false);
		topNField.setForeground(Color.GRAY);
	}
	
	void topNSearchButtonInit() {
		topNSearchButton = new JButton("Search");
		topNSearchButton.setFont(new Font("Times", Font.BOLD, 15));
		topNSearchButton.setBounds(400, 400, 200, 50);
		topNSearchButton.setVisible(false);
	}
	
	void goBackSearchButtonInit() {
		goBackSearchButton = new JButton("Go Back To Search");
		goBackSearchButton.setFont(new Font("Times", Font.BOLD, 15));
		goBackSearchButton.setBounds(780, 10, 200, 30);
		goBackSearchButton.setVisible(false);
	}
	
	public void submitJobToGCP() throws InterruptedException, ExecutionException {
		String region = "us-west1";
		String myClusterName = "qix22-cluster-7d04";
		String myEndpoint = String.format("%s-dataproc.googleapis.com:443", region);
		String mainClass = "invertedIndex";
		String projectId = "cs1660-273621";


		try {
			JobControllerSettings jobControllerSettings = JobControllerSettings.newBuilder().setEndpoint(myEndpoint).build();
			JobControllerClient jobControllerClient = JobControllerClient.create(jobControllerSettings);
			
			JobPlacement jobPlacement = JobPlacement.newBuilder().setClusterName(myClusterName).build();
			HadoopJob myJob = HadoopJob.newBuilder().setMainClass(mainClass)
					.setMainClass("invertedIndex")
					.addJarFileUris("gs://dataproc-staging-us-west1-305420578024-hrm4rnvu/JAR/invertedindex.jar")
					.addArgs("gs://dataproc-staging-us-west1-305420578024-hrm4rnvu/Data")
					.addArgs("gs://dataproc-staging-us-west1-305420578024-hrm4rnvu/Data/output")
					.build();
			Job job = Job.newBuilder().setPlacement(jobPlacement).setHadoopJob(myJob).build();
			Job request = jobControllerClient.submitJob(projectId, region, job);

			String jobId = request.getReference().getJobId();
			System.out.println(String.format("Submitted job " + jobId));
			
			System.out.println("Job running......");
			
			// Wait for the job to finish.
			CompletableFuture<Job> finishedJobFuture = CompletableFuture.supplyAsync(() -> waitForJobCompletion(jobControllerClient, projectId, region, jobId));
			int timeout = 10;
			try {
				Job jobInfo = finishedJobFuture.get(timeout, TimeUnit.MINUTES);
				System.out.println(String.format("Job %s finished successfully.", jobId));
				System.out.println("jobInfo: " + jobInfo);
			} catch (TimeoutException e) {
				System.err.println(
				String.format("Job timed out after %d minutes: %s", timeout, e.getMessage()));
			}			
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
	
	public static Job waitForJobCompletion(JobControllerClient jobControllerClient, String projectId, String region, String jobId) {
		while (true) {
			// Poll the service periodically until the Job is in a finished state.
			Job jobInfo = jobControllerClient.getJob(projectId, region, jobId);
			switch (jobInfo.getStatus().getState()) {
				case DONE:
				case CANCELLED:
				case ERROR:
					return jobInfo;
				default:
					try {
						// Wait a second in between polling attempts.
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
			}
		}
	}
	
	public void getFileChooserPage() {
		filesNameDisplay.selectAll();
		filesNameDisplay.replaceSelection("");

		fc.setCurrentDirectory(new File("."));
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setMultiSelectionEnabled(true);

	    int select = fc.showOpenDialog(this); // Display file chooser
	    
	    if(select == JFileChooser.APPROVE_OPTION) // Choose yes
	    {
		    File[] files = fc.getSelectedFiles();
		    for(File file : files)
		    {
				System.out.println("File "+file.getName()+" is selected");
				filesNameDisplay.append(file.getName()+"\n");
		    }
	    }
	    else // Cancel choose
		    System.out.println("Cancel selection operation");
	}
	
	public void getSuccessfulPage() {
		textArea.selectAll();
		textArea.replaceSelection("");
		textArea.append("                          Engine was loaded\n");
		textArea.append("\n");
		textArea.append("                                        &\n");
		textArea.append("\n");
		textArea.append("  Inverted indicies were constructed successfully!\n");
		textArea.append("\n");
		textArea.append("\n");
		textArea.append("                         Please Select Action");
		textArea.setBounds(210, 100, 700, 220);
		textArea.setFont(new Font("Times", Font.BOLD, 20));
		
		chooseFilesButton.setVisible(false);
		constructButton.setVisible(false);
		termSearchButton.setVisible(false);
    	termField.setVisible(false);
		topNSearchButton.setVisible(false);
    	topNField.setVisible(false);
    	goBackSearchButton.setVisible(false);

		filesNameDisplay.selectAll();
		filesNameDisplay.replaceSelection("");
		filesNameDisplay.setVisible(false);

		searchTermButton.setVisible(true);
		topNButton.setVisible(true);
	}
	
	public void getSearchTermPage() {
		textArea.selectAll();
		textArea.replaceSelection("Enter Your Search Term");
    	textArea.setBounds(362, 160, 300, 30);
    	
    	termField.addFocusListener(new JTextFieldHintListener(termField, "Type Your Search Here ..."));

    	searchTermButton.setVisible(false);
    	topNButton.setVisible(false);

    	termSearchButton.setVisible(true);
    	termField.setVisible(true);
	}
	
	public void getSearchTopNPage() {
		textArea.selectAll();
		textArea.replaceSelection("Enter Your N Value");
    	textArea.setBounds(392, 160, 300, 30);
    	
		topNField.addFocusListener(new JTextFieldHintListener(topNField, "Type Your N Here ..."));

    	searchTermButton.setVisible(false);
    	topNButton.setVisible(false);

    	topNSearchButton.setVisible(true);
    	topNField.setVisible(true);
	}
	
	public void getTermResultPage(String storedTerm) {
		Instant start = Instant.now();
    	String output = getOutput(storedTerm);
    	System.out.println("Raw output is: " + output);
    	Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
    	
        String[] out = output.trim().split("\\s+");
        Object[][] wordInfo = new Object[out.length - 1][3];
    	for (int i = 1; i < out.length; i++) {
    		String[] pair = out[i].split(":");
    		wordInfo[i-1][0] = i;
    		wordInfo[i-1][1] = pair[0];
    		wordInfo[i-1][2] = pair[1];
    	}
    	
    	String[] name = { "                  Doc ID", 
    					  "                Doc Name", 
    					  "             Frequencies"};
    	DefaultTableModel defaultModel = new DefaultTableModel(wordInfo, name); // Using default table model
    	
    	JTable table = new JTable(defaultModel); // Create table object
    	
    	DefaultTableCellRenderer r = new DefaultTableCellRenderer();   
		r.setHorizontalAlignment(JLabel.CENTER);   
		table.setDefaultRenderer(Object.class, r);
    	
    	JTableHeader head = table.getTableHeader(); // Create head object
        head.setPreferredSize(new Dimension(head.getWidth(), 40));// Set head size
        head.setFont(new Font("Times", Font.BOLD, 20));// Set head front	        
        table.setRowHeight(90);
        
        table.setFont(new Font("Times", Font.PLAIN, 15)); // Set data front
	
    	jsp = new JScrollPane(table);
    	jsp.setBackground(Color.WHITE);
    	jsp.setBounds(30, 240, 940, 250);
        
    	contentPane.add(jsp);

    	termField.setText("");

    	textArea.selectAll();
		textArea.replaceSelection("You searched for the term: " + storedTerm + "\n");
		textArea.append("\n");
		textArea.append("Your search was executed in " + timeElapsed + " ms\n");
		textArea.setFont(new Font("Times", Font.PLAIN, 15));
    	textArea.setBounds(30, 120, 900, 60);

    	termSearchButton.setVisible(false);
    	termField.setVisible(false);

    	goBackSearchButton.setVisible(true);
	}
 	
	public String getOutput(String word) {
		String projectId = "cs1660-273621";
		String bucketName = "dataproc-staging-us-west1-305420578024-hrm4rnvu";
		String outputPrefix = "Data/output/part-r-";
		
		System.out.println("Outputing...");
		
		final com.google.cloud.storage.Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
	    Bucket bucket = storage.get(bucketName);
	    Page<Blob> blobs = bucket.list(Storage.BlobListOption.currentDirectory(), Storage.BlobListOption.prefix(outputPrefix));
	    int i = 1;
	    
	    for (Blob blob : blobs.iterateAll()) {
	    	System.out.print("File" + i++ + ": ");
	    	System.out.println(blob.getName());
	    	byte[] content = blob.getContent();
	    	String s = new String(content);
	    	String[] str = s.split("\n");
	    	for(int j = 0; j < str.length; j++) {
	    		String[] tokens = str[j].split("\\s+");
	    		//System.out.println("Check line " + j + "     /" + tokens[0] + "     /" + word + "     /" + str[j]);
	    		if(tokens[0].equals(word)) {
	    			System.out.println("Finished printing");
	    			return str[j];
	    		}
	    	}
	    }
	    
	    System.out.println("Finished printing"); 
	    return word;
	}
	
	public void getTopNResultPage(String storedN) {
		topNField.setText("");

    	textArea.selectAll();
		textArea.replaceSelection("Top-" + storedN);
		textArea.append(" Frequent Terms");
		textArea.setFont(new Font("Times", Font.PLAIN, 15));
    	textArea.setBounds(30, 80, 900, 30);

    	topNSearchButton.setVisible(false);
    	topNField.setVisible(false);

    	goBackSearchButton.setVisible(true);
	}
	
  	public static void main(String[] args) {
  		System.out.println("Start My Engine");
    	new searchEngine();
	}
}
