/*
 * Generated Mon Jan 19 16:12:12 PST 2015
 */

	.section ".rodata"
	.align 4
_endl:	.asciz "\n"
_intFmt:	.asciz "%d"
_strFmt:	.asciz "%s"
_boolT:	.asciz "true"
_boolF:	.asciz "false"
arrrray_error:	.asciz "Index value of %d is outside legal range [0,%d).\n"
del_errrror:	.asciz "Attempt to dereference NULL pointer.\n"

	.section ".data"
	.align 4
.globalInit:	.word 0

! ---f---
	.section ".text"
	.align 4
	.global f
f:
	set			SAVE.f, %g1
	save		%sp, %g1, %sp


	mov			%fp, %l1
	set			4, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %i0
	ret
	restore


	ret
	restore



	SAVE.f = -(92 + 4) & -8
