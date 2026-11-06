# R304 2026–2027 — notes for Claude

Teaching material for R3.04 "Qualité de développement" (BUT Informatique, Mikal Ziane): the assignment sheets and
the starting sources that students clone. Everything student-facing is in French. The project was first developed
with ChatGPT; the git history (French commit subjects, no bodies) is the only log of that work.

## Machines
Several machines are in use, each with its own clone; pull before working, push every commit. Known clones:
- `zenbook-mikal`: `C:\repos\R304_2026`.
Add a line here when a new machine is used (`hostname` gives its name).

## Handover (2026-11-06)
- TP1 and TD1 run before the first lecture. TP1 starts with two tests. TD1 starts with two tests and asks students to read the five Java files, trace the evaluator call and draw dependency arrows.
- The full KRK engine and the OracleTerminal exercise are in Git history. The current TD1 uses a two-position example. Its student fallback archive and the teacher materials are managed separately in Dropbox.
- For TP2 and TD2 onward, keep one folder, one SUJET.md and one pom.xml per session. Link each session from README.md.

## Layout
- `README.md`: the course index students see first (links to the sheets, the AI-use rule).
- `TP1/`: practical session 1 "Encapsulation et changements", Maven multi-module (`pile`, `refuge`), JUnit 5, Java 21.
  `TP1/SUJET.md` is the sheet; it also holds the Git procedure students follow once (private repo `R304-travaux`
  as `origin`, the course repo as `upstream`).
- TD1: "Découvrir une séparation du programme". See TD1/ORIGINE.md for the provenance and retained licence.
- `.idea/` is git-ignored; the Maven wrappers (`mvnw`, `mvnw.cmd`, Maven 3.9.9, script-only) are committed so
  students need no Maven install.

## Remotes
The names below apply to the clone listed above. On other machines, inspect `git remote -v` before pushing.
- `upstream` = `mikalziane/R304-2026-2027`, the public course repository students clone. Pushing there publishes.
- `origin` = `mikalziane/R304-travaux`, a private copy (the same setup the sheet asks students to make).
Pull before working, push every commit to both remotes once the user says the material is ready; the local clone
has been ahead of both remotes by several commits at times.

## Commands (JDK 21 or newer; the machine has JDK 23 and Maven 3.9.16)
- TP1: `cd TP1 && mvn -q test` (or `.\mvnw.cmd test`), 2 tests. Run the programs the way the sheet says:
  `java -cp pile/target/classes piles.Main` and `java -cp refuge/target/classes Main` (the refuge reads stdin).
- TD1: open `TD1/pom.xml` and run `ChoisirPositionTest`, or run `.\mvnw.cmd clean test` from `TD1`. Expect two tests.
- Checking a sheet: do the exercise in a scratch copy and make sure the counts and commands the sheet gives
  (test totals, class and method names, paths) match the code; the sheets quote them literally.

## Conventions
- Sheets are plain Markdown, one `SUJET.md` per session, timed sections, French typography (« », ’, —).
  Mentioned APIs must exist with the exact names.
- Starting sources must stay minimal and must compile and pass their tests as delivered; the TP1 sources are
  deliberately badly encapsulated (that is the exercise), do not "fix" them.
- The sheets forbid AI use by students during sessions; keep that rule in `README.md`.
- Commit messages: a short French subject in the imperative, as in the existing history. Stage files by name.
- Any change to `TD1/src` that comes from the chessKRK repository should be recorded in `TD1/ORIGINE.md`.
