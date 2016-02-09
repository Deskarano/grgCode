package grgCode;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GUIHandler
{
    //initialize frame
    private static JFrame component_mainFrame;

    //initialize components
    private static JTextArea component_input;
    private static JTextArea component_output;
    private static JTextArea component_variables;
    private static JScrollPane component_inputScrollPane;
    private static JScrollPane component_outputScrollPane;
    private static JScrollPane component_variablesScrollPane;

    //initialize constraints
    private static GridBagConstraints constraints_input;
    private static GridBagConstraints constraints_output;
    private static GridBagConstraints constraints_variables;

    //initialize menu
    private static JMenuBar menu_mainBar;

    private static JMenu menu_file;
    private static JMenu menu_file_new;
    private static JMenu menu_edit;
    private static JMenu menu_program;

    private static JMenuItem menu_file_save;
    private static JMenuItem menu_file_saveas;
    private static JMenuItem menu_file_open;
    private static JMenuItem menu_file_clear;
    private static JMenuItem menu_file_new_program;
    private static JMenuItem menu_program_run;
    private static JMenuItem menu_program_stepbystep;

    //initialize inputs
    public static String component_output_text = "";
    public static String component_variables_text = "";

    //initialize programs
    private static Program program_currentProgram;

    public static void main(String[] args)
    {
        //instantiate frame
        instantiate_frame();

        //instantiate component variables
        instantiate_components();

        //instantiate menu variables
        instantiate_menu();

        //instantiate constraints
        instantiate_constraints();

        //build frame and show it
        instantiate_finalize();
    }
    
    private static void instantiate_frame()
    {
        component_mainFrame = new JFrame("grgCode");
        component_mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        component_mainFrame.setLayout(new GridBagLayout());
        component_mainFrame.setResizable(false);
    }
    
    private static void instantiate_components()
    {
        component_input = new JTextArea();    
        component_output = new JTextArea();
        component_variables = new JTextArea();
    
        component_output.setEditable(false);
        component_variables.setEditable(false);
    
        component_inputScrollPane = new JScrollPane(component_input);
        component_outputScrollPane = new JScrollPane(component_output);
        component_variablesScrollPane = new JScrollPane(component_variables);
    }
    
    private static void instantiate_menu()
    {
        //create main
        menu_mainBar = new JMenuBar();
    
        //create main/
        menu_file = new JMenu("File");
        menu_edit = new JMenu("Edit");
        menu_program = new JMenu("Program");
    
        //create main/file
        menu_file_open = new JMenuItem("Open");
        menu_file_save = new JMenuItem("Save");
        menu_file_saveas = new JMenuItem("Save As");
        menu_file_open = new JMenuItem("Open");
        menu_file_clear = new JMenuItem("Clear");
    
        //create main/file/new
        menu_file_new = new JMenu("New");
        menu_file_new_program = new JMenuItem("Program");
    
        //create main/program
        menu_program_run = new JMenuItem("Run");
        menu_program_stepbystep = new JMenuItem("Step-by-step");
    
        //TODO: ADD FUNCTIONALITY
        menu_file_clear.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent arg0) 
                {
                    component_input.setText("");
                }
        
            });
    
        menu_program_run.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e) 
                {
                    update_output_clear();
                    program_currentProgram = null;
                    program_currentProgram = new Program(component_input.getText());
                    program_currentProgram.program_preprocess();
                    program_currentProgram.program_process();
                }
            });
        
        menu_program_stepbystep.addActionListener(new ActionListener()
            {
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
		    if(program_currentProgram == null)
		    {
			program_currentProgram = new Program(component_input.getText());
			program_currentProgram.program_preprocess();
		    }
		    
		    program_currentProgram.program_processLine();
		}          
            });
    
    
        //put everything together
        menu_file_new.add(menu_file_new_program);
        menu_file.add(menu_file_new);
        menu_file.add(menu_file_save);
        menu_file.add(menu_file_saveas);
        menu_file.add(menu_file_open);
        menu_file.add(menu_file_clear);
    
        menu_program.add(menu_program_run);
        menu_program.add(menu_program_stepbystep);

        menu_mainBar.add(menu_file);
        menu_mainBar.add(menu_edit);
        menu_mainBar.add(menu_program);
    }
    
    private static void instantiate_constraints()
    {
        constraints_input = new GridBagConstraints();
        constraints_input.gridx = 0;
        constraints_input.gridy = 0;
        constraints_input.gridwidth = 8;
        constraints_input.gridheight = 6;
        constraints_input.ipadx = 480;
        constraints_input.ipady = 360;
        constraints_input.insets = new Insets(5, 5, 5, 5);
        constraints_input.fill = GridBagConstraints.BOTH;
    
        constraints_output = new GridBagConstraints();
        constraints_output.gridx = 0;
        constraints_output.gridy = 6;
        constraints_output.gridwidth = 8;
        constraints_output.gridheight = 6;
        constraints_output.ipadx = 480;
        constraints_output.ipady = 360;
        constraints_output.insets = new Insets(5, 5, 5, 5);
        constraints_output.fill = GridBagConstraints.BOTH;
    
        constraints_variables = new  GridBagConstraints();
        constraints_variables.gridx = 10;
        constraints_variables.gridy = 4;
        constraints_variables.gridwidth = 2;
        constraints_variables.gridheight = 3;
        constraints_variables.ipadx = 240;
        constraints_variables.ipady = 360;
        constraints_variables.insets = new Insets(5, 5, 5, 5);
        constraints_variables.fill = GridBagConstraints.BOTH;
    }
    
    private static void instantiate_finalize()
    {
	component_mainFrame.add(component_inputScrollPane, constraints_input);
	component_mainFrame.add(component_outputScrollPane, constraints_output);
	component_mainFrame.add(component_variablesScrollPane, constraints_variables);
    
	component_mainFrame.setJMenuBar(menu_mainBar);
    
	component_mainFrame.pack();
	component_mainFrame.setVisible(true);
    }
    
    public static void update_output(String update)
    {
        component_output_text += update;
        component_output.setText(component_output_text);
    }
    
    public static void update_output_clear()
    {
        component_output_text = "";
        component_output.setText(component_output_text);
    }
    
    public static void update_variables(String update)
    {
        component_variables_text += update;
        component_variables.setText(component_variables_text);
    }
    
    public static void update_variables_clear()
    {
        component_variables_text = "";
        component_variables.setText(component_variables_text);
    }
}
