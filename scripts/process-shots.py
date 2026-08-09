#!/usr/bin/env python3
"""Turn raw `adb screencap` frames into Play-ready screenshots, plus a bezel check for each.

A Wear screenshot has two jobs and one file cannot do both:

    <name>.png         the shot as the watch drew it, 24-bit RGB — the form a Play listing takes.
    <name>-bezel.png   the same shot with everything outside the round display painted magenta.

`adb screencap` returns the square framebuffer, but a round watch only lights the inscribed
circle, so an unmasked screenshot hides exactly the clipping a Wear release gets rejected for
("no text or controls are cut off by the screen edges"). Magenta touching a glyph or a control
means the layout overflows the bezel on a real watch. The bezel copy is a check, not an asset —
nothing with magenta corners belongs on a listing.

Play's requirements for the plain copy are asserted here because Play's own rejection names none
of them: 24-bit PNG (screencap writes RGBA), square, and at least 384x384.

Usage: process-shots.py <dir>   # converts every raw-*.png in <dir>
"""

import sys
from pathlib import Path

from PIL import Image, ImageDraw

MIN_EDGE = 384
MAGENTA = (255, 0, 255)


def process(raw: Path) -> list[str]:
    name = raw.name[len("raw-"):]
    plain_path = raw.with_name(name)
    bezel_path = raw.with_name(name.replace(".png", "-bezel.png"))

    image = Image.open(raw).convert("RGB")  # screencap writes RGBA; Play accepts 24-bit only
    width, height = image.size
    image.save(plain_path)

    mask = Image.new("L", (width, height), 0)
    ImageDraw.Draw(mask).ellipse((0, 0, width - 1, height - 1), fill=255)
    masked = Image.new("RGB", (width, height), MAGENTA)
    masked.paste(image, mask=mask)
    masked.save(bezel_path)

    problems = []
    if width != height:
        problems.append(f"not square ({width}x{height})")
    if min(width, height) < MIN_EDGE:
        problems.append(f"{width}x{height} is below Play's {MIN_EDGE}x{MIN_EDGE} minimum")
    return problems


def main() -> int:
    directory = Path(sys.argv[1] if len(sys.argv) > 1 else "play-assets/screenshots")
    raws = sorted(directory.glob("raw-*.png"))
    if not raws:
        print(f"no raw-*.png in {directory}", file=sys.stderr)
        return 1

    failed = False
    for raw in raws:
        problems = process(raw)
        status = "; ".join(problems) if problems else "ok — 24-bit RGB, square, >=384"
        print(f"{raw.name[len('raw-'):]:<28} {status}")
        failed |= bool(problems)
    return 1 if failed else 0


if __name__ == "__main__":
    raise SystemExit(main())
