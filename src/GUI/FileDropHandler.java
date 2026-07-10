package GUI;

import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;

public class FileDropHandler extends DropTargetAdapter {
    private final Consumer<File> onFileDropped;

    public FileDropHandler(Component target, Consumer<File> onFileDropped){
        this.onFileDropped = onFileDropped;
        new DropTarget(target, DnDConstants.ACTION_COPY, this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void drop(DropTargetDropEvent event){
        event.acceptDrop(DnDConstants.ACTION_COPY);
        try {
            Transferable transferable = event.getTransferable();
            if(!transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
                event.dropComplete(false);
                return;
            }
            List<File> files = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
            event.dropComplete(true);
            if(!files.isEmpty()){
                File file = files.get(0);
                SwingUtilities.invokeLater(() -> onFileDropped.accept(file));
            }
        } catch (Exception ex){
            event.dropComplete(false);
        }
    }
}
