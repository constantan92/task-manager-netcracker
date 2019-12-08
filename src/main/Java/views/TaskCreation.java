package views;

import controllers.TasksController;
import models.Task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

public class TaskCreation extends JDialog {
    private JPanel contentPane;
    private JButton buttonAdd;
    private JButton buttonCancel;
    private JTextField dateField;
    private JTextArea descriptionArea;
    private JTextField nameField;

    private JFrame frame;
    private TasksController controller;

    public TaskCreation(JFrame frame, TasksController controller) {
        super(frame, "Task creation", true);
        this.frame = frame;
        this.controller = controller;
        setContentPane(contentPane);
        setSize(new Dimension(500, 350));
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        getRootPane().setDefaultButton(buttonAdd);

        descriptionArea.setBorder(UIManager.getBorder("TextField.border"));

        buttonAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void onOK() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy dd.MM HH:mm");
        LocalDateTime dueDate = LocalDateTime.now();

        if(nameField.getText().length() == 0 || nameField.getText().length() > 24)
            new ErrorDialog((JFrame)getParent(), ErrorType.NAME_LENGTH);
        else if(descriptionArea.getText().length() > 256)
            new ErrorDialog((JFrame)getParent(), ErrorType.DESCRIPTION_LENGTH);
        else {
            boolean dateError = false;
            try {
                dueDate = LocalDateTime.parse(dateField.getText(), formatter);
            }
            catch (DateTimeParseException e) {
                dateError = true;
                new ErrorDialog(frame, ErrorType.DATE_FORMAT);
            }

            if(!dateError && LocalDateTime.now().compareTo(dueDate) >= 0) {
                dateError = true;
                new ErrorDialog(frame, ErrorType.DATE_ALREADY_PAST);
            }

            if(!dateError) {
                controller.add(new Task(UUID.randomUUID(), nameField.getText(), descriptionArea.getText(),
                        LocalDateTime.now(), LocalDateTime.parse(dateField.getText(), formatter),
                        UUID.randomUUID(), 0));
                dispose();
            }
        }
    }

    private void onCancel() {
        dispose();
    }

}