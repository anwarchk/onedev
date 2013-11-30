package com.pmease.gitop.core.hookcallback;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pmease.commons.git.Commit;
import com.pmease.commons.util.StringUtils;
import com.pmease.gitop.core.manager.BranchManager;
import com.pmease.gitop.core.manager.ProjectManager;
import com.pmease.gitop.model.Branch;
import com.pmease.gitop.model.Project;

@SuppressWarnings("serial")
@Singleton
public class PostReceiveServlet extends CallbackServlet {

    public static final String PATH = "/git-post-receive";
    
    private static final Logger logger = LoggerFactory.getLogger(PostReceiveServlet.class);

    private final BranchManager branchManager;
    
    @Inject
    public PostReceiveServlet(ProjectManager projectManager, BranchManager branchManager) {
        super(projectManager);
        
        this.branchManager = branchManager;
    }

    @Override
    protected void callback(Project project, String callbackData, Output output) {
		List<String> splitted = StringUtils.splitAndTrim(callbackData, " ");

		String oldCommitHash = splitted.get(0);
		String branchName = splitted.get(2);

		// User with write permission can create new branch
		if (oldCommitHash.equals(Commit.ZERO_HASH)) {
			Branch branch = new Branch();
			branch.setProject(project);
			branch.setName(branchName);
			branchManager.save(branch);
			return;
		}
		
		logger.info("Executing post-receive hook against branch {}...", branchName);
    }
    
}
