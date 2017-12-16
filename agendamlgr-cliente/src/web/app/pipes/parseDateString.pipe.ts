import { Pipe, PipeTransform } from "@angular/core";

@Pipe({ name: 'parseDateString' })
export class ParseDateStringPipe implements PipeTransform {

    transform(value: string): Date {
        if(value) {
            let dateStr = value.replace("[UTC]", "");
            return new Date(Date.parse(dateStr));
        } else {
            return null
        }
    }

}