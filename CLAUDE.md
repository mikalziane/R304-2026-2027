# R304 2026–2027 — notes for Claude

Teaching material for R3.04 "Qualité de développement" (BUT Informatique, Mikal Ziane): the assignment sheets and
the starting sources that students clone. Everything student-facing is in French. The project was first developed
with ChatGPT; the git history (French commit subjects, no bodies) is the only log of that work.

## Machines
Several machines are in use, each with its own clone; pull before working, push every commit. Known clones:
- `zenbook-mikal`: `C:\repos\R304_2026`.
Add a line here when a new machine is used (`hostname` gives its name).

## Handover (as of 2026-11-06)
- What it is: the sheets and starting sources of R3.04 for 2026–2027, two sessions so far (TP1, TD1), both
  "before the first lecture". Students clone `upstream`; corrections go to Moodle, not here.
- State: everything delivered builds and passes as the sheets say (TP1 2 tests, TD1 33; doing the TD1 exercise in
  a scratch copy gives 36 tests). The commands quoted in the sheets were run and work. `TD1/SUJET.md` mentions a
  fallback `base-etudiante.zip` that is not in the repository (handed out separately, to be produced from `TD1/`).
- Last content change: commit 0e3d2a8 (Rules/Square capacities computed from `BOARD_SIZE`), pushed to `upstream`.
- Next: push the same commits to both remotes whenever the material changes; produce the TD1 zip if still needed;
  add the following sessions (TP2, TD2, ...) in the same shape, one folder, one `SUJET.md`, one `pom.xml` each,
  and link them from `README.md`.

## Layout
- `README.md`: the course index students see first (links to the sheets, the AI-use rule).
- `TP1/`: practical session 1 "Encapsulation et changements", Maven multi-module (`pile`, `refuge`), JUnit 5, Java 21.
  `TP1/SUJET.md` is the sheet; it also holds the Git procedure students follow once (private repo `R304-travaux`
  as `origin`, the course repo as `upstream`).
- `TD1/`: tutorial 1 "Tester le choix d'un coup avec une simulation" for the FI students: a copy of the `After/`
  KRK engine from `PuckSearchTeam/chessKRK` (commit ef5fc12, see `TD1/ORIGINE.md`, Apache 2.0 licence kept),
  with the package `org.chesskrk.adapter.pedagogie` already declared in `CleanArchitectureTest`. Students write
  `OracleTerminal` in that package and `TD1Test`.
- `.idea/` is git-ignored; the Maven wrappers (`mvnw`, `mvnw.cmd`, Maven 3.9.9, script-only) are committed so
  students need no Maven install.

## Remotes
- `upstream` = `mikalziane/R304-2026-2027`, the public course repository students clone. Pushing there publishes.
- `origin` = `mikalziane/R304-travaux`, a private copy (the same setup the sheet asks students to make).
Pull before working, push every commit to both remotes once the user says the material is ready; the local clone
has been ahead of both remotes by several commits at times.

## Commands (JDK 21 or newer; the machine has JDK 23 and Maven 3.9.16)
- TP1: `cd TP1 && mvn -q test` (or `.\mvnw.cmd test`), 2 tests. Run the programs the way the sheet says:
  `java -cp pile/target/classes piles.Main` and `java -cp refuge/target/classes Main` (the refuge reads stdin).
- TD1: `cd TD1 && mvn -q package`, 33 tests (35 after the two tests of the sheet, 36 with the optional third);
  the jar is `TD1/target/krk-after.jar`, a UCI engine (`uci`, `isready`, `position fen ...`, `go`, `quit`).
- Checking a sheet: do the exercise in a scratch copy and make sure the counts and commands the sheet gives
  (test totals, class and method names, paths) match the code; the sheets quote them literally.

## Conventions
- Sheets are plain Markdown, one `SUJET.md` per session, timed sections, French typography (« », ’, —).
  Mentioned APIs must exist with the exact names (`Rules.status`, `Evaluation.loss(0)`, `Square.parse`, ...).
- Starting sources must stay minimal and must compile and pass their tests as delivered; the TP1 sources are
  deliberately badly encapsulated (that is the exercise), do not "fix" them.
- The sheets forbid AI use by students during sessions; keep that rule in `README.md`.
- Commit messages: a short French subject in the imperative, as in the existing history. Stage files by name.
- Any change to `TD1/src` that comes from the chessKRK repository should be recorded in `TD1/ORIGINE.md`.
