import fs from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const BASE_URL = 'http://localhost';
const TOTAL_URLS = 100;

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const OUTPUT_FILE = path.join(__dirname, 'generated-shortcodes.json');

const shortCodes = [];

console.log(`Creating ${TOTAL_URLS} short URLs...\n`);

for (let i = 0; i < TOTAL_URLS; i++) {

    const response = await fetch(`${BASE_URL}/api/v1/urls`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            longUrl: `https://example.com/page-${i}`
        })
    });

    if (!response.ok) {
        console.error(`Failed to create URL ${i + 1}`);
        process.exit(1);
    }

    const body = await response.json();

    shortCodes.push(body.shortCode);

    process.stdout.write(`Created ${i + 1}/${TOTAL_URLS}\r`);
}

await fs.writeFile(
    OUTPUT_FILE,
    JSON.stringify(shortCodes, null, 2)
);

console.log("\n");
console.log("================================");
console.log("Seed completed successfully.");
console.log(`Generated ${shortCodes.length} short codes.`);
console.log(`Saved to ${OUTPUT_FILE}`);
console.log("================================");