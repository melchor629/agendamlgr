import { Pipe, PipeTransform } from "@angular/core";
const commonmark = require('commonmark');

@Pipe({ name: 'md' })
export class MarkdownPipe implements PipeTransform {

    private reader = new commonmark.Parser();
    private writer = new commonmark.HtmlRenderer({ safe: true });

    transform(md: string): any {
        if(md) {
            return this.writer.render(
                this.reader.parse(
                    md.split('\n')
                        .map(str => str.replace(/^#+/g, ''))
                        .join('\n')
                )
            );
        } else {
            return md;
        }
    }

}