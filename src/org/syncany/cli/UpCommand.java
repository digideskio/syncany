package org.syncany.cli;

import static java.util.Arrays.asList;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.syncany.operations.StatusOperation.ChangeSet;
import org.syncany.operations.StatusOperation.StatusOperationOptions;
import org.syncany.operations.UpOperation.SyncUpOperationOptions;
import org.syncany.operations.UpOperation.SyncUpOperationResult;

public class UpCommand extends Command {
	@Override
	public int execute(String[] operationArgs) throws Exception {
		SyncUpOperationOptions operationOptions = parseOptions(operationArgs);
		SyncUpOperationResult operationResult = client.up(operationOptions);	
		
		printResults(operationResult);
		
		return 0;
	}

	public SyncUpOperationOptions parseOptions(String[] operationArgs) throws Exception {
		// Sync up options
		SyncUpOperationOptions operationOptions = new SyncUpOperationOptions();

		OptionParser parser = new OptionParser();	
		parser.allowsUnrecognizedOptions();
		
		OptionSpec<Void> optionNoCleanup = parser.acceptsAll(asList("c", "no-cleanup"));
		OptionSpec<Void> optionForceUpload = parser.acceptsAll(asList("F", "force-upload"));
		
		OptionSet options = parser.parse(operationArgs);
		
		// status [<args>]
		operationOptions.setStatusOptions(parseStatusOptions(operationArgs));
		
		// --no-cleanup
		operationOptions.setCleanupEnabled(!options.has(optionNoCleanup));
		
		// --force
		operationOptions.setForceUploadEnabled(options.has(optionForceUpload));
		
		return operationOptions;
	}
	
	private StatusOperationOptions parseStatusOptions(String[] operationArgs) {
		StatusCommand statusCommand = new StatusCommand();
		return statusCommand.parseOptions(operationArgs);
	}

	public void printResults(SyncUpOperationResult operationResult) {
		ChangeSet changeSet = operationResult.getChangeSet();
		
		if (changeSet.hasChanges()) {			
			for (String newFile : changeSet.getNewFiles()) {
				out.println("A "+newFile);
			}
	
			for (String changedFile : changeSet.getChangedFiles()) {
				out.println("M "+changedFile);
			}
			
			for (String deletedFile : changeSet.getDeletedFiles()) {
				out.println("D "+deletedFile);
			}	
			
			out.println("Sync up finished.");
		}
		else {
			out.println("Sync up skipped, no local changes.");
		}
	}

}