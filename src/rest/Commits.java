package rest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import models.Branch;
import models.Commit;
import models.FileDiff;

@Path("/git")
public class Commits {
	
	private static final String GIT_PROJECT_PATH = "/Users/matt/Documents/workspace/project2/.git";
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("branches/{branch_id}")
	public List<Commit> test3(@PathParam("branch_id") int branchId) throws IOException, NoHeadException, GitAPIException {
		Git git = Git.open(new File(GIT_PROJECT_PATH));
		Iterable<RevCommit> log = git.log().add(git.branchList().call().get(branchId).getObjectId().toObjectId()).call();
		ArrayList<Commit> commits = new ArrayList<Commit>();
		for (RevCommit message : log) {
			Commit commit = new Commit();
			commit.setHash(message.name().toString());
			commit.setShortMessage(message.getShortMessage());
			commit.setMessage(message.getFullMessage());
			commit.setAuthor(message.getCommitterIdent().getName());
			commit.setEmail(message.getCommitterIdent().getEmailAddress());
			commits.add(commit);
		}
		return commits;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("branches")
	public List<Branch> branches() throws GitAPIException, IOException {
		Git git = Git.open(new File(GIT_PROJECT_PATH));
		List<Ref> refBranches = git.branchList().call();
		ArrayList<Branch> branches = new ArrayList<Branch>();
		int i = 0;
		for (Ref refBranch : refBranches) {
			Branch branch = new Branch();
			branch.setName(refBranch.getName());
			branch.setId(i++);
			branches.add(branch);
		}
		return branches;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("commits/{sha}")
	public List<FileDiff> commit(@PathParam("sha") String sha) throws IOException, GitAPIException {
		Git git = Git.open(new File(GIT_PROJECT_PATH));
		RevCommit revCommit = new RevWalk(git.getRepository()).parseCommit(ObjectId.fromString(sha));
		RevCommit parentCommit = new RevWalk(git.getRepository()).parseCommit(revCommit.getParent(0).toObjectId());
		
		ObjectReader reader = git.getRepository().newObjectReader();
		CanonicalTreeParser newTreeParser = new CanonicalTreeParser();
		newTreeParser.reset(reader, revCommit.getTree().toObjectId());
		CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
		oldTreeParser.reset(reader, parentCommit.getTree().toObjectId());
		
		List<DiffEntry> diffEntries = git.diff()
				.setNewTree(newTreeParser)
				.setOldTree(oldTreeParser).call();
		
		ArrayList<FileDiff> fileDiffs = new ArrayList<FileDiff>();
		for (DiffEntry diff : diffEntries) {
			FileDiff fileDiff = new FileDiff();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			DiffFormatter df = new DiffFormatter(output);
			df.setRepository(git.getRepository());
			df.format(diff);
			fileDiff.setFileName(diff.getNewPath());
			fileDiff.setDiff(output.toString());
			fileDiffs.add(fileDiff);
		}
		return fileDiffs;
	}
}
