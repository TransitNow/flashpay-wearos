# Skills in this repo

- `wear-os-guidelines` — a **symlink** to `../../../cli-tools/skills/wear-os-guidelines`, the Wear
  OS Play-compliance guardrails shared by every watch project and owned by the `cli-tools` repo.
  Model-invocable: `SKILL.md` carries no `disable-model-invocation` flag, so Claude loads it on
  its own before wear UI changes and releases. It is deliberately **not** installed at user scope
  (`~/.claude/skills/`) — that made it visible in every unrelated session.

The symlink assumes `cli-tools` is checked out as a sibling of this repo
(`code/<this-project>` next to `code/cli-tools`). Clone this project without it and the link
dangles — the skill is then silently absent, not an error. To repair, clone `cli-tools` beside
this repo, or point the link at wherever it lives.
