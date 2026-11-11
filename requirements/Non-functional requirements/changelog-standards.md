# Changelog Standards

## Convention

This project follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) with [SemVer](https://semver.org/) versioning. The changelog lives in a dedicated `CHANGELOG.md` at the project root, linked from `README.md`.

## Format

```markdown
## [Unreleased]

## [x.y.z] - YYYY-MM-DD
### Added
- New features
### Changed
- Changes in existing functionality
### Deprecated
- Soon-to-be removed features
### Removed
- Removed features
### Fixed
- Bug fixes
### Security
- Vulnerability fixes
```

## Rules

**Must:**
- Newest version first (reverse chronological order)
- Keep an `[Unreleased]` section at the top for new work
- Use ISO 8601 date format (`YYYY-MM-DD`)
- Version numbers follow SemVer: `MAJOR.MINOR.PATCH`
- One entry per change, written in simple English
- Link `CHANGELOG.md` from `README.md`

**Must Not:**
- Never modify or delete a released version entry (append-only)
- No vague entries (e.g. "bug fixes" without saying what was fixed)
- No code snippets or implementation details in entries

## Release Flow

1. New changes go into `[Unreleased]` as they are completed
2. On release, rename `[Unreleased]` to `[x.y.z] - YYYY-MM-DD`
3. Add a fresh `[Unreleased]` section above it
4. Commit and push

## Definition of Done

1. Every user-facing change has a changelog entry in `[Unreleased]`
2. Format follows the categories above
3. Released entries are never edited afterwards