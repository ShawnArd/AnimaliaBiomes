"""Generate tools/puzzle-gallery.html showing every authored puzzle.

Re-run this any time puzzles change:
    cd AnimaliaBiomes
    python tools/generate_gallery.py
"""
import json
import os
from html import escape

BIOMES = ['aquatic', 'grassland', 'forest', 'desert', 'tundra']
PUZZLES_DIR = 'app/src/main/assets/puzzles'
OUT_PATH = 'tools/puzzle-gallery.html'

CELL_PX = {5: 18, 10: 12, 15: 10, 20: 8}
SIZE_BY_STAGE_ID = {1: 5, 2: 10, 3: 15, 4: 20}


def render_grid(puzzle: dict) -> str:
    size = puzzle['size']
    palette = puzzle['palette']
    solution = puzzle['solution']
    cell_px = CELL_PX.get(size, 12)

    cells = []
    for row in solution:
        for ch in row:
            if ch in ('.', ' '):
                cells.append('<div class="cell empty"></div>')
            else:
                color = palette.get(ch, '#FF00FF')
                cells.append(f'<div class="cell" style="background:{color}"></div>')

    style = f'grid-template-columns: repeat({size}, {cell_px}px); grid-auto-rows: {cell_px}px;'
    return f'<div class="puzzle-grid" style="{style}">{"".join(cells)}</div>'


def render_palette(palette: dict) -> str:
    swatches = []
    for key, color in palette.items():
        swatches.append(
            f'<div class="swatch" style="background:{color}" title="{key}={color}">{escape(key)}</div>'
        )
    return f'<div class="palette">{"".join(swatches)}</div>'


def render_puzzle(puzzle: dict) -> str:
    name = escape(puzzle['name'])
    pid = puzzle['id']
    size = puzzle['size']
    return f'''
    <div class="puzzle-card" data-size="{size}">
      <div class="puzzle-header">
        <span class="puzzle-id">#{pid}</span>
        <span class="puzzle-name">{name}</span>
        <span class="puzzle-size">{size}×{size}</span>
      </div>
      {render_grid(puzzle)}
      {render_palette(puzzle['palette'])}
    </div>'''


def render_stage(stage: dict) -> str:
    sid = stage['id']
    size = SIZE_BY_STAGE_ID.get(sid, '?')
    target = stage['targetPuzzleCount']
    n = len(stage['puzzles'])
    cards = ''.join(render_puzzle(p) for p in stage['puzzles'])
    if not cards:
        cards = '<div class="empty-stage">No puzzles authored yet.</div>'
    return f'''
    <div class="stage">
      <h3>{escape(stage['name'])} <span class="stage-info">{size}×{size} · {n}/{target} puzzles</span></h3>
      <div class="puzzle-row">{cards}</div>
    </div>'''


def render_biome(biome: dict) -> str:
    stages = ''.join(render_stage(s) for s in biome['stages'])
    total = sum(len(s['puzzles']) for s in biome['stages'])
    return f'''
    <section class="biome" id="biome-{biome['id']}">
      <h2>{escape(biome['name'])} <span class="biome-info">{total} puzzles</span></h2>
      {stages}
    </section>'''


def main() -> None:
    biomes = []
    for bn in BIOMES:
        path = os.path.join(PUZZLES_DIR, f'{bn}.json')
        with open(path, encoding='utf-8') as f:
            biomes.append(json.load(f))

    nav = ''.join(
        f'<a href="#biome-{b["id"]}">{escape(b["name"])}</a>'
        for b in biomes
    )
    sections = ''.join(render_biome(b) for b in biomes)
    total = sum(len(s['puzzles']) for b in biomes for s in b['stages'])

    html = f'''<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Puzzle Gallery — Animalia Biomes</title>
<style>
  :root {{
    --ocean-deep: #0D47A1;
    --ocean-mid: #1565C0;
    --ocean-light: #1E88E5;
    --foam: #B3E5FC;
  }}
  * {{ box-sizing: border-box; }}
  body {{
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
    background: linear-gradient(180deg, var(--ocean-deep) 0%, var(--ocean-mid) 50%, var(--ocean-light) 100%);
    background-attachment: fixed;
    color: #fff;
    margin: 0;
    padding: 0;
    min-height: 100vh;
  }}
  header {{
    background: rgba(13, 71, 161, 0.95);
    padding: 16px 24px;
    border-bottom: 1px solid rgba(255,255,255,0.2);
    position: sticky; top: 0; z-index: 10;
    backdrop-filter: blur(8px);
  }}
  header h1 {{ margin: 0 0 8px; font-size: 1.4em; }}
  header .summary {{ font-size: 0.9em; color: var(--foam); }}
  nav {{
    margin-top: 8px;
    display: flex; gap: 8px; flex-wrap: wrap;
  }}
  nav a {{
    color: #fff; text-decoration: none;
    padding: 4px 12px;
    border-radius: 4px;
    background: rgba(255,255,255,0.1);
    font-size: 0.85em;
  }}
  nav a:hover {{ background: rgba(255,255,255,0.25); }}
  main {{ padding: 24px; max-width: 1600px; margin: 0 auto; }}
  .biome {{
    margin-bottom: 48px;
    background: rgba(255,255,255,0.05);
    border-radius: 12px;
    padding: 20px;
    border: 1px solid rgba(255,255,255,0.1);
  }}
  .biome h2 {{
    margin: 0 0 16px;
    padding-bottom: 8px;
    border-bottom: 1px solid rgba(255,255,255,0.2);
    font-size: 1.4em;
  }}
  .biome-info {{ font-size: 0.7em; color: var(--foam); font-weight: 400; margin-left: 8px; }}
  .stage {{ margin-top: 20px; }}
  .stage h3 {{
    margin: 0 0 12px;
    font-size: 1em;
    font-weight: 600;
    color: var(--foam);
  }}
  .stage-info {{ font-size: 0.85em; color: rgba(255,255,255,0.6); font-weight: 400; margin-left: 6px; }}
  .puzzle-row {{
    display: flex; flex-wrap: wrap; gap: 12px;
  }}
  .puzzle-card {{
    background: rgba(0,0,0,0.2);
    border: 1px solid rgba(255,255,255,0.1);
    border-radius: 6px;
    padding: 10px;
    display: flex; flex-direction: column;
    align-items: center;
  }}
  .puzzle-header {{
    width: 100%;
    margin-bottom: 8px;
    font-size: 0.78em;
    display: flex; justify-content: space-between; align-items: baseline; gap: 6px;
  }}
  .puzzle-id {{ color: rgba(255,255,255,0.5); }}
  .puzzle-name {{ font-weight: 600; flex: 1; text-align: center; }}
  .puzzle-size {{ color: rgba(255,255,255,0.5); font-size: 0.9em; }}
  .puzzle-grid {{
    display: grid;
    background: rgba(255,255,255,0.05);
    padding: 2px;
    border-radius: 2px;
    gap: 0;
  }}
  .cell {{ background: transparent; }}
  .palette {{
    display: flex; gap: 4px;
    margin-top: 8px;
    flex-wrap: wrap; justify-content: center;
  }}
  .swatch {{
    width: 18px; height: 18px;
    border-radius: 50%;
    border: 1px solid rgba(255,255,255,0.3);
    display: flex; align-items: center; justify-content: center;
    font-size: 10px;
    font-weight: 700;
    color: rgba(0,0,0,0.7);
    text-shadow: 0 0 1px rgba(255,255,255,0.5);
  }}
  .empty-stage {{
    color: rgba(255,255,255,0.4);
    font-style: italic;
    padding: 8px;
  }}
</style>
</head>
<body>
<header>
  <h1>Animalia Biomes — Puzzle Gallery</h1>
  <div class="summary">{len(biomes)} biomes · {total} puzzles · Generated from <code>app/src/main/assets/puzzles/*.json</code></div>
  <nav>{nav}</nav>
</header>
<main>
{sections}
</main>
</body>
</html>'''

    with open(OUT_PATH, 'w', encoding='utf-8') as f:
        f.write(html)
    print(f'Wrote {OUT_PATH} ({len(html):,} chars, {total} puzzles across {len(biomes)} biomes)')


if __name__ == '__main__':
    main()
