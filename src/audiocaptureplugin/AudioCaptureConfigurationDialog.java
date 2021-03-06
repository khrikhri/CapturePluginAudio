package audiocaptureplugin;
import java.awt.*;
import java.awt.event.*;
import java.util.ResourceBundle;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import mo.core.ui.GridBConstraints;
import mo.core.ui.Utils;
import mo.organization.ProjectOrganization;

public class AudioCaptureConfigurationDialog extends JDialog implements DocumentListener {

    JLabel errorLabel;
    JTextField nameField;
    JButton accept;
    ProjectOrganization org;
    JComboBox cbMic;
    JSpinner sSR;
    int SR;
    int op_mic;
    ResourceBundle dialogBundle = java.util.ResourceBundle.getBundle("properties/principal");
        

    boolean accepted = false;

    public AudioCaptureConfigurationDialog() {
        super(null, "Audio Capture Configuration", Dialog.ModalityType.APPLICATION_MODAL);
    }

    public AudioCaptureConfigurationDialog(ProjectOrganization organization) {
        super(null, "Audio Capture Configuration", Dialog.ModalityType.APPLICATION_MODAL);
        org = organization;
    }

    public boolean showDialog() {

        setLayout(new GridBagLayout());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                accepted = false;
                super.windowClosing(e);
            }
        });

        setLayout(new GridBagLayout());
        GridBConstraints gbc = new GridBConstraints();

        JLabel label = new JLabel(dialogBundle.getString("configuration_n"));
        JLabel mic = new JLabel(dialogBundle.getString("select_d"));
        JLabel samplerate = new JLabel(dialogBundle.getString("select_sr"));
        sSR = new JSpinner(new SpinnerNumberModel(8000,4000,120000,250));
        
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        int i=0;
	for (Mixer.Info info: mixerInfos){
		Mixer m = AudioSystem.getMixer(info);
		Line.Info[] lineInfos = m.getTargetLineInfo();
		if(lineInfos.length>=1 && lineInfos[0].getLineClass().equals(TargetDataLine.class)){//Only prints out info is it is a Microphone
		    i++;		
		}
	}        
        String[ ] microphones = new String[i];
        i=0;
        for (Mixer.Info info: mixerInfos){
		Mixer m = AudioSystem.getMixer(info);
		Line.Info[] lineInfos = m.getTargetLineInfo();
		if(lineInfos.length>=1 && lineInfos[0].getLineClass().equals(TargetDataLine.class)){//Only prints out info is it is a Microphone
		    microphones[i]=info.getName();
                    i++;
		}
	}
        cbMic = new JComboBox(microphones);
        if(i>0){cbMic.setSelectedIndex(1);}
        nameField = new JTextField();
        nameField.getDocument().addDocumentListener(this);
        sSR.setValue(8000);

        gbc.gx(0).gy(0).f(GridBConstraints.HORIZONTAL).a(GridBConstraints.FIRST_LINE_START).i(new Insets(5, 5, 5, 5));
        add(label, gbc);
        add(nameField, gbc.gx(2).wx(1).gw(3));        
        add(mic, gbc.gy(2).gx(0));
        add(cbMic,gbc.gx(2).gy(2).wx(1).gw(3));       
        add(samplerate, gbc.gy(4).gx(0));
        add(sSR,gbc.gx(2).gy(4).wx(1).gw(2));
        
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.red);
        add(errorLabel, gbc.gx(0).gy(7).gw(5).a(GridBConstraints.LAST_LINE_START).wy(1));
        accept = new JButton(dialogBundle.getString("accept"));
        
        accept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accepted = true;                
                op_mic=cbMic.getSelectedIndex();
                SR=(int) sSR.getValue();
                setVisible(false);
                dispose();
            }
        });

        gbc.gx(0).gy(6).a(GridBConstraints.LAST_LINE_END).gw(3).wy(1).f(GridBConstraints.NONE);
        add(accept, gbc);

        setMinimumSize(new Dimension(400, 150));
        setPreferredSize(new Dimension(400, 300));
        pack();
        Utils.centerOnScreen(this);
        updateState();
        setVisible(true);

        return accepted;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateState();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateState();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updateState();
    }

    private void updateState() {        
        if (nameField.getText().isEmpty()) {
            errorLabel.setText(dialogBundle.getString("name"));
            accept.setEnabled(false);
        } else {
            errorLabel.setText("");
            accept.setEnabled(true);
        }
    }

    public String getConfigurationName() {
        return nameField.getText();
    }
}
