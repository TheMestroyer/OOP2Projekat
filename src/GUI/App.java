package GUI;

import Abstractions.*;
import Helpers.FileManager;
import Helpers.InactivityWatcher;
import Helpers.TimeTracker;

import javax.swing.Timer;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.function.Consumer;

public class App extends Frame {
    private InspectorPanel inspectorPanel;
    private FilterPanel filterPanel;
    private SimulationPanel simPanel;
    private DataTablePanel dataTablePanel;
    private SimControlPanel simControlPanel;

    private Dialog inactivityDialog;

    private void SetupPanels(){
        this.setVisible(true);

        SimClock.Init();
        TimeTracker.Init();

        simPanel = new SimulationPanel();
        simPanel.setVisible(true);
        new FileDropHandler(simPanel, this::handleDroppedFile);
        filterPanel = new FilterPanel();
        filterPanel.setVisible(true);
        inspectorPanel = new InspectorPanel(this::onDataChanged);
        inspectorPanel.setVisible(true);
        dataTablePanel = new DataTablePanel();
        dataTablePanel.setVisible(true);
        simControlPanel = new SimControlPanel(this::onDataChanged);
        simControlPanel.setVisible(true);

        FlightManager.GetInstance();
    }

    private void onDataChanged(){
        filterPanel.refresh();
        dataTablePanel.refresh();
        simPanel.repaint();
    }

    @Override
    public void paint(Graphics g) {
        DrawAPIAWT.init(g);
    }

    private void SetupLayout(){
        SetupPanels();

        setName("BASEM (Best airplane sim ever made)");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setSize(new Dimension(1280,720));
        setLayout(new BorderLayout());

        Panel centerPanel = new Panel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill    = GridBagConstraints.BOTH;
        c.weighty = 1.0;

        c.weightx = 0.0;
        c.gridx = 0;
        centerPanel.add(filterPanel,c);
        c.weightx = 1.0;
        c.gridx = 1;
        centerPanel.add(simPanel,c);
        c.weightx = 0.0;
        c.gridx = 2;
        centerPanel.add(inspectorPanel,c);

        add(simControlPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(dataTablePanel, BorderLayout.SOUTH);

        setupMenuBar();
        setupInactivityWatcher();
    }

    private void setupMenuBar(){
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");

        MenuItem saveCsvItem = new MenuItem("Save as CSV...");
        MenuItem saveJsonItem = new MenuItem("Save as JSON...");
        MenuItem loadCsvItem = new MenuItem("Load CSV...");
        MenuItem loadJsonItem = new MenuItem("Load JSON...");

        saveCsvItem.addActionListener(e -> handleSave(FileManager::SaveCsv, "csv"));
        saveJsonItem.addActionListener(e -> handleSave(FileManager::SaveJson, "json"));
        loadCsvItem.addActionListener(e -> handleLoad(FileManager::LoadCsv, "csv"));
        loadJsonItem.addActionListener(e -> handleLoad(FileManager::LoadJson, "json"));

        fileMenu.add(saveCsvItem);
        fileMenu.add(saveJsonItem);
        fileMenu.addSeparator();
        fileMenu.add(loadCsvItem);
        fileMenu.add(loadJsonItem);

        menuBar.add(fileMenu);
        setMenuBar(menuBar);
    }

    private void handleSave(Consumer<File> saver, String extension){
        FileDialog fd = new FileDialog(this, "Save as ."+extension, FileDialog.SAVE);
        fd.setFile("simulation."+extension);
        fd.setVisible(true);
        if(fd.getFile()==null) return;

        File file = new File(fd.getDirectory(), fd.getFile());
        try {
            saver.accept(file);
        } catch (RuntimeException ex){
            showMessage("Save failed", ex.getMessage());
        }
    }

    private void handleLoad(Consumer<File> loader, String extension){
        FileDialog fd = new FileDialog(this, "Load ."+extension+" file", FileDialog.LOAD);
        fd.setVisible(true);
        if(fd.getFile()==null) return;

        loadFile(new File(fd.getDirectory(), fd.getFile()), loader);
    }

    private void loadFile(File file, Consumer<File> loader){
        try {
            loader.accept(file);
            onDataChanged();
        } catch (RuntimeException ex){
            showMessage("Load failed", ex.getMessage());
        }
    }

    private void handleDroppedFile(File file){
        String name = file.getName().toLowerCase();
        if(name.endsWith(".csv")) loadFile(file, FileManager::LoadCsv);
        else if(name.endsWith(".json")) loadFile(file, FileManager::LoadJson);
        else showMessage("Load failed", "Unsupported file type '"+file.getName()+"'. Drop a .csv or .json file.");
    }

    private void showMessage(String title, String message){
        Dialog dialog = new Dialog(this, title, true);
        dialog.setBackground(UIConsts.BackgroundColor1);
        dialog.setLayout(new BorderLayout(10,10));
        TextArea messageArea = new TextArea(message, 4, 40, TextArea.SCROLLBARS_NONE);
        messageArea.setEditable(false);
        messageArea.setBackground(UIConsts.BackgroundColor1);
        messageArea.setForeground(UIConsts.TextColor);
        Button okButton = new Button("OK");
        okButton.setForeground(UIConsts.TextColor);
        okButton.addActionListener(e -> dialog.dispose());
        dialog.add(messageArea, BorderLayout.CENTER);
        dialog.add(okButton, BorderLayout.SOUTH);
        dialog.setSize(420,150);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void setupInactivityWatcher(){
        InactivityWatcher watcher = InactivityWatcher.GetInstance();
        watcher.SetPausePredicate(() -> SimData.GetInstance().hasSelectedAirport() || TimeTracker.GetInstance().IsActive());
        watcher.SetOnWarn(this::showInactivityWarning);
        watcher.SetOnTimeout(() -> System.exit(0));

        Toolkit.getDefaultToolkit().addAWTEventListener(
                e -> watcher.NotifyAction(),
                AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK
        );
    }

    private void showInactivityWarning(){
        if(inactivityDialog!=null && inactivityDialog.isVisible()) return;

        inactivityDialog = new Dialog(this, "Still there?", true);
        inactivityDialog.setBackground(UIConsts.BackgroundColor1);
        inactivityDialog.setLayout(new BorderLayout(10,10));

        int[] remaining = {InactivityWatcher.TIMEOUT_SECONDS - InactivityWatcher.WARNING_SECONDS};
        Label countdownLabel = new Label("Closing in "+remaining[0]+" seconds due to inactivity.");
        countdownLabel.setForeground(UIConsts.TextColor);
        Button continueButton = new Button("Continue");
        continueButton.setForeground(UIConsts.TextColor);

        inactivityDialog.add(countdownLabel, BorderLayout.CENTER);
        inactivityDialog.add(continueButton, BorderLayout.SOUTH);
        inactivityDialog.setSize(360,120);
        inactivityDialog.setLocationRelativeTo(this);

        Timer countdown = new Timer(1000, null);
        countdown.addActionListener(e -> {
            remaining[0]--;
            if(remaining[0]<=0){
                countdown.stop();
                inactivityDialog.dispose();
                System.exit(0);
            } else {
                countdownLabel.setText("Closing in "+remaining[0]+" seconds due to inactivity.");
            }
        });
        countdown.start();

        continueButton.addActionListener(e -> {
            countdown.stop();
            InactivityWatcher.GetInstance().NotifyAction();
            inactivityDialog.dispose();
        });
        inactivityDialog.addWindowListener(new WindowAdapter(){
            @Override public void windowClosing(WindowEvent e){
                countdown.stop();
                InactivityWatcher.GetInstance().NotifyAction();
                inactivityDialog.dispose();
            }
        });

        inactivityDialog.setVisible(true);
    }

    public App(){
        super();
        SetupLayout();
        setVisible(true);
    }
}
